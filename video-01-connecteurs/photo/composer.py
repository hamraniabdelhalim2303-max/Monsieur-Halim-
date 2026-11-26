# Compose la photo : personne détourée (pixels du visage inchangés) + salle de classe floutée.
from PIL import Image, ImageFilter, ImageEnhance, ImageChops
import numpy as np
H = 3780                                                  # on coupe le bas (filigrane « realme »)
person = Image.open('photo/cut_birefnet-portrait.png').convert('RGBA').crop((0, 0, 1840, H))
bg = Image.open('photo/fond-classe-net.png').convert('RGB')
bg = bg.filter(ImageFilter.GaussianBlur(22))              # profondeur de champ (effet portrait)
bg = ImageEnhance.Brightness(bg).enhance(0.97)
# Retouche douce et globale de la personne : netteté légère, contraste, couleurs (aucune déformation)
rgb, a = person.convert('RGB'), person.getchannel('A')
rgb = rgb.filter(ImageFilter.UnsharpMask(radius=2, percent=60, threshold=3))
rgb = ImageEnhance.Contrast(rgb).enhance(1.06)
rgb = ImageEnhance.Color(rgb).enhance(1.05)
# léger lissage du bord du détourage pour bien l'intégrer
a = a.filter(ImageFilter.MinFilter(5)).filter(ImageFilter.GaussianBlur(1.5))   # resserre le bord (supprime le liseré clair)
# ombre portée douce derrière la personne
sh = Image.new('L', bg.size, 0); sh.paste(a.point(lambda v: int(v * 0.35)), (25, 30)); sh = sh.filter(ImageFilter.GaussianBlur(40))
bg = Image.composite(Image.new('RGB', bg.size, (40, 35, 30)), bg, sh)
out = bg.copy(); out.paste(rgb, (0, 0), a)
# léger vignettage
w, h = out.size; yy, xx = np.mgrid[0:h, 0:w]
v = 1 - 0.18 * (((xx - w/2) / (w/2))**2 + ((yy - h*0.4) / (h*0.6))**2)
out = Image.fromarray((np.asarray(out).astype(float) * np.clip(v, 0.75, 1)[..., None]).clip(0, 255).astype(np.uint8))
out.save('photo/photo-salle-de-classe.jpg', quality=95, subsampling=0)
out.resize((w//4, h//4), Image.LANCZOS).save('/tmp/claude-0/-home-user-Monsieur-Halim-/948a08ac-5f38-5a46-bba8-7d53ac2fe94f/scratchpad/compo.png')
print(out.size)
