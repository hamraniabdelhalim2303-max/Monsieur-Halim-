# Génère les variantes de couleur du logo à partir de logo-original.png
from PIL import Image
import numpy as np, sys
src = Image.open('logo/logo-original.png').convert('RGB')
src = src.crop((40, 148, 490, 410))                     # recadrage serré
W, H = src.size
src = src.resize((W*4, H*4), Image.LANCZOS)              # agrandi pour la vidéo
a = np.asarray(src).astype(float)
d = 255 - a.mean(-1)                                     # « encre » : 0 = blanc
ink = np.clip((d - 70) / 70, 0, 1)                       # texte + plume
book = np.clip(d / 45, 0, 1) * (1 - ink)                 # livre (gris clair)
def save(name, ink_rgb, book_rgb, book_op=0.85):
    out = np.zeros((a.shape[0], a.shape[1], 4))
    al = ink + book * book_op
    for c in range(3):
        out[..., c] = np.where(al > 0, (ink*ink_rgb[c] + book*book_op*book_rgb[c]) / np.maximum(al, 1e-6), 0)
    out[..., 3] = np.clip(al, 0, 1) * 255
    Image.fromarray(out.astype(np.uint8), 'RGBA').save(f'logo/{name}.png')
IV, GO, NA, INK = (250,246,236), (201,169,97), (27,42,74), (26,26,26)
save('logo-ivoire-or', IV, GO)          # pour fonds bleu marine (vidéo)
save('logo-marine-or', NA, GO)          # pour fonds ivoire / blanc
save('logo-noir', INK, (150,150,150), 0.6)   # version d'origine détourée
# calque de la plume, utilisé seul par animate.py
x_split = int((405 - 40) * 4)
for nm, m in (('anim-plume', ink*(np.arange(a.shape[1]) >= x_split)),):
    col = IV
    out = np.zeros((a.shape[0], a.shape[1], 4)); out[..., :3] = col; out[..., 3] = np.clip(m, 0, 1)*255
    Image.fromarray(out.astype(np.uint8), 'RGBA').save(f'logo/src-{nm}.png')
print(a.shape)
