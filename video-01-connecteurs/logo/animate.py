# Animation du logo : balayage lumineux qui révèle livre + texte, puis la plume se pose, puis le slogan.
# Sortie : frames RGB brutes 1920x1080 sur stdout (à envoyer dans ffmpeg).
import sys, numpy as np
from PIL import Image
FPS, DUR, LW = 30, 4.8, 1150
bg = np.asarray(Image.open('src/anim-bg.png').convert('RGB')).astype(np.float32)
slog = np.asarray(Image.open('src/anim-slogan.png').convert('RGBA')).astype(np.float32)
def load(path):
    im = Image.open(path).convert('RGBA'); h = round(im.height * LW / im.width)
    return np.asarray(im.resize((LW, h), Image.LANCZOS)).astype(np.float32)
full, plume = load('logo/logo-ivoire-or.png'), load('logo/src-anim-plume.png')
base = full.copy(); base[..., 3] = np.clip(full[..., 3] - plume[..., 3], 0, 255)
H, W = full.shape[:2]; X0, Y0 = (1920 - W)//2, 150
SPLIT = int(W * (405 - 40) / 450)                     # début de la zone de la plume
right = base.copy(); right[:, :SPLIT, 3] = 0; base[:, SPLIT:, 3] = 0   # partie droite : apparaît avec la plume
GOLD = np.array([232, 205, 140], np.float32)
ease = lambda t: 0. if t <= 0 else 1. if t >= 1 else 1 - (1 - t)**3
def comp(frame, layer, x, y, alpha=1.0):
    h, w = layer.shape[:2]; a = layer[..., 3:4] / 255 * alpha
    reg = frame[y:y+h, x:x+w]; reg[:] = reg * (1 - a) + layer[..., :3] * a
cols = np.arange(W, dtype=np.float32)
for i in range(int(FPS * DUR)):
    t = i / FPS; f = bg.copy()
    p = ease((t - 0.2) / 2.2); edge = -150 + p * (W + 300)
    if p > 0:
        m = np.clip((edge - cols) / 150, 0, 1)                      # zone révélée
        glow = np.exp(-((cols - edge + 40) / 45) ** 2) * (p < 1)     # liseré lumineux
        lay = base.copy(); lay[..., 3] *= m[None, :]
        lay[..., :3] = lay[..., :3] * (1 - glow[None, :, None]) + GOLD * glow[None, :, None]
        comp(f, lay, X0, Y0)
    e = ease((t - 1.3) / 0.9)
    if e > 0: comp(f, right, X0, Y0, e); comp(f, plume, X0, Y0 - int(40 * (1 - e)), e)
    e = ease((t - 2.8) / 0.7)
    if e > 0: comp(f, slog, 0, 0, e)
    fo = 1.0
    if t > DUR - 0.4: fo = (DUR - t) / 0.4
    sys.stdout.buffer.write((f * fo).clip(0, 255).astype(np.uint8).tobytes())
