// Bibliothèque d'illustrations vectorielles — style « papier découpé » Monsieur Halim
// Chaque animal est dessiné face à gauche, pieds à y=0, centré en x=0. Taille de référence : lion adulte ≈ 420 px de long.

const C = {
  ivory: '#FAF6EC', sand: '#E9DCC0', navy: '#1B2A4A', forest: '#2F5D3A', sage: '#A9BFA0', gold: '#C9A961',
  lion: '#D9A045', lionLight: '#EFC77A', lionDark: '#B57A2C', mane: '#8A4A1C', maneLight: '#A8622A',
  ele: '#9AA0A6', eleDark: '#7D838A', eleLight: '#B9BEC2',
  gir: '#E8B864', girSpot: '#A0612A', monkey: '#7A5236', monkeyFace: '#E4C29A',
  croc: '#5E7A3E', crocDark: '#46602D', cheetah: '#E2B358', ink: '#2A2118'
};

const defs = () => `
<defs>
  <filter id="soft" x="-20%" y="-20%" width="140%" height="140%"><feGaussianBlur stdDeviation="10"/></filter>
  <filter id="blur3"><feGaussianBlur stdDeviation="3"/></filter>
  <filter id="haze"><feGaussianBlur stdDeviation="2.2"/></filter>
  <radialGradient id="sheen" cx="35%" cy="30%" r="70%"><stop offset="0" stop-color="#fff" stop-opacity=".35"/><stop offset="1" stop-color="#fff" stop-opacity="0"/></radialGradient>
  <linearGradient id="shade" x1="0" y1="0" x2="0" y2="1"><stop offset=".55" stop-color="#000" stop-opacity="0"/><stop offset="1" stop-color="#000" stop-opacity=".22"/></linearGradient>
  <linearGradient id="skySunrise" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#F3C98B"/><stop offset=".45" stop-color="#F8E2B8"/><stop offset="1" stop-color="#FBEFD6"/></linearGradient>
  <linearGradient id="skyDay" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#CFE3E6"/><stop offset=".6" stop-color="#F3EBD6"/><stop offset="1" stop-color="#F8EFD9"/></linearGradient>
  <radialGradient id="sunGlow" cx="50%" cy="50%" r="50%"><stop offset="0" stop-color="#FFF3CF" stop-opacity="1"/><stop offset=".35" stop-color="#FFE3A0" stop-opacity=".8"/><stop offset="1" stop-color="#FFE3A0" stop-opacity="0"/></radialGradient>
  <linearGradient id="ground1" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#C9B77A"/><stop offset="1" stop-color="#B4A464"/></linearGradient>
  <linearGradient id="ground2" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#9FAE6A"/><stop offset="1" stop-color="#7E9354"/></linearGradient>
  <linearGradient id="ground3" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#6E8A4A"/><stop offset="1" stop-color="#4F6D38"/></linearGradient>
  <radialGradient id="ivoryGlow" cx="50%" cy="50%" r="50%"><stop offset="0" stop-color="#FAF6EC" stop-opacity="1"/><stop offset=".6" stop-color="#FAF6EC" stop-opacity=".85"/><stop offset="1" stop-color="#FAF6EC" stop-opacity="0"/></radialGradient>
</defs>`;

const g = (x, y, s, inner, flip = false) =>
  `<g transform="translate(${x} ${y}) scale(${flip ? -s : s} ${s})">${inner}</g>`;

const shadow = (w, op = .28) => `<ellipse cx="0" cy="4" rx="${w}" ry="${w * .12}" fill="#3a2f1a" opacity="${op}" filter="url(#soft)"/>`;

// ---------- décor ----------
function acacia(x, y, s = 1, col = '#3F5E32', trunk = '#5A4630') {
  return g(x, y, s, `
    <path d="M-6 0 L-4 -120 L-40 -170 M-4 -120 L30 -175 M-4 -120 L-2 -185" stroke="${trunk}" stroke-width="12" fill="none" stroke-linecap="round"/>
    <path d="M-6 0 L-4 -120" stroke="${trunk}" stroke-width="18" stroke-linecap="round"/>
    <ellipse cx="-10" cy="-190" rx="150" ry="34" fill="${col}"/>
    <ellipse cx="-60" cy="-205" rx="80" ry="24" fill="${col}"/>
    <ellipse cx="50" cy="-200" rx="90" ry="26" fill="${col}"/>
    <ellipse cx="-10" cy="-215" rx="70" ry="20" fill="${col}" opacity=".9"/>
    <ellipse cx="-20" cy="-198" rx="140" ry="10" fill="#fff" opacity=".08"/>`);
}
function grassTufts(y, n, col, seed = 1, h = 40, x0 = 0, x1 = 1920) {
  let s = seed, out = '';
  const r = () => (s = (s * 9301 + 49297) % 233280) / 233280;
  for (let i = 0; i < n; i++) {
    const x = x0 + r() * (x1 - x0), hh = h * (.6 + r() * .8);
    out += `<path d="M${x - 10} ${y} Q${x - 6} ${y - hh * .6} ${x - 14} ${y - hh} M${x} ${y} Q${x + 2} ${y - hh * .7} ${x + 4} ${y - hh * 1.15} M${x + 9} ${y} Q${x + 12} ${y - hh * .5} ${x + 20} ${y - hh * .85}" stroke="${col}" stroke-width="4" fill="none" stroke-linecap="round"/>`;
  }
  return out;
}
function savanna({ sky = 'skySunrise', sun = true, sunX = 1300, sunY = 430, mountains = true, trees = [[260, 640, .9], [1650, 610, .7]], horizon = 640, water = false } = {}) {
  let out = `<rect width="1920" height="1080" fill="url(#${sky})"/>`;
  if (sun) out += `<circle cx="${sunX}" cy="${sunY}" r="360" fill="url(#sunGlow)"/><circle cx="${sunX}" cy="${sunY}" r="95" fill="#FFF1C6"/>`;
  if (mountains) out += `
    <path d="M0 ${horizon - 40} L180 ${horizon - 150} L340 ${horizon - 90} L520 ${horizon - 190} L760 ${horizon - 70} L980 ${horizon - 160} L1220 ${horizon - 60} L1460 ${horizon - 170} L1700 ${horizon - 90} L1920 ${horizon - 140} L1920 ${horizon} L0 ${horizon} Z" fill="#D9C7A6" opacity=".75" filter="url(#haze)"/>
    <path d="M0 ${horizon - 10} L260 ${horizon - 80} L520 ${horizon - 30} L800 ${horizon - 95} L1100 ${horizon - 25} L1400 ${horizon - 85} L1700 ${horizon - 30} L1920 ${horizon - 70} L1920 ${horizon} L0 ${horizon} Z" fill="#BFB58E" opacity=".8"/>`;
  out += `<rect y="${horizon - 4}" width="1920" height="${1080 - horizon + 4}" fill="url(#ground1)"/>`;
  out += trees.map(t => acacia(t[0], t[1], t[2], '#556F3F')).join('');
  out += `<path d="M0 ${horizon + 90} Q480 ${horizon + 50} 960 ${horizon + 85} T1920 ${horizon + 70} L1920 1080 L0 1080 Z" fill="url(#ground2)"/>`;
  if (water) out += `<ellipse cx="${water.x}" cy="${water.y}" rx="${water.rx}" ry="${water.rx * .14}" fill="#8FB8BE" opacity=".9"/><ellipse cx="${water.x - 30}" cy="${water.y - 4}" rx="${water.rx * .6}" ry="${water.rx * .05}" fill="#fff" opacity=".35"/>`;
  out += `<path d="M0 ${horizon + 260} Q600 ${horizon + 220} 1100 ${horizon + 250} T1920 ${horizon + 240} L1920 1080 L0 1080 Z" fill="url(#ground3)"/>`;
  out += grassTufts(horizon + 95, 60, '#8C9C58', 3, 28);
  out += grassTufts(1080, 70, '#3E5A2E', 7, 70);
  return out;
}

// ---------- animaux ----------
function lion({ mane = true, color = C.lion, maneCol = C.mane, mouth = 'closed' } = {}) {
  const dark = C.lionDark;
  const maneTufts = mane ? Array.from({ length: 18 }, (_, i) => {
    const a = i / 18 * Math.PI * 2, r = 92;
    return `<ellipse cx="${-150 + Math.cos(a) * r * .78}" cy="${-205 + Math.sin(a) * r * .9}" rx="40" ry="26" transform="rotate(${a * 57.3} ${-150 + Math.cos(a) * r * .78} ${-205 + Math.sin(a) * r * .9})" fill="${i % 2 ? maneCol : C.maneLight}"/>`;
  }).join('') : '';
  return `${shadow(230)}
  <!-- pattes arrière-plan -->
  <rect x="-120" y="-120" width="34" height="122" rx="16" fill="${dark}"/>
  <rect x="95" y="-120" width="36" height="122" rx="16" fill="${dark}"/>
  <!-- queue -->
  <path d="M175 -150 C250 -150 255 -60 290 -40" stroke="${color}" stroke-width="14" fill="none" stroke-linecap="round"/>
  <ellipse cx="292" cy="-36" rx="16" ry="22" fill="${maneCol}"/>
  <!-- corps -->
  <path d="M-170 -150 C-170 -230 -60 -245 60 -235 C160 -228 205 -190 200 -130 C196 -80 150 -75 60 -78 C-40 -80 -170 -70 -170 -150 Z" fill="${color}"/>
  <path d="M-170 -150 C-170 -230 -60 -245 60 -235 C160 -228 205 -190 200 -130 C196 -80 150 -75 60 -78 C-40 -80 -170 -70 -170 -150 Z" fill="url(#shade)"/>
  <ellipse cx="0" cy="-200" rx="150" ry="30" fill="url(#sheen)"/>
  <!-- pattes avant-plan -->
  <path d="M-150 -110 L-150 -8 Q-150 4 -130 4 L-100 4 Q-96 -6 -112 -12 L-112 -110 Z" fill="${color}"/>
  <path d="M120 -110 L118 -8 Q118 4 138 4 L166 4 Q170 -6 154 -12 L160 -120 Z" fill="${color}"/>
  ${maneTufts}
  ${mane ? `<ellipse cx="-150" cy="-205" rx="88" ry="96" fill="${maneCol}"/><ellipse cx="-110" cy="-150" rx="60" ry="70" fill="${C.maneLight}" opacity=".9"/>` : ''}
  <!-- tête -->
  <ellipse cx="-160" cy="-215" rx="58" ry="62" fill="${color}"/>
  <ellipse cx="-125" cy="-262" rx="15" ry="13" fill="${color}"/><ellipse cx="-125" cy="-262" rx="7" ry="6" fill="${dark}"/>
  <ellipse cx="-196" cy="-188" rx="38" ry="30" fill="${C.lionLight}"/>
  <path d="M-224 -206 L-204 -206 L-214 -194 Z" fill="${C.ink}"/>
  <path d="M-214 -194 L-214 -182 M-214 -182 Q-224 -172 -232 -178 M-214 -182 Q-204 -172 -196 -178" stroke="${C.ink}" stroke-width="3" fill="none" stroke-linecap="round"/>
  <ellipse cx="-184" cy="-232" rx="9" ry="7" fill="#fff"/><circle cx="-187" cy="-232" r="5.5" fill="#5a3a14"/><circle cx="-189" cy="-234" r="2" fill="#fff"/>
  <path d="M-198 -244 Q-184 -250 -172 -244" stroke="${dark}" stroke-width="3" fill="none"/>
  `;
}
function cub() {
  return `${shadow(120)}
  <rect x="-58" y="-62" width="22" height="64" rx="10" fill="${C.lionDark}"/><rect x="48" y="-62" width="22" height="64" rx="10" fill="${C.lionDark}"/>
  <path d="M90 -80 C130 -90 140 -40 160 -40" stroke="${C.lionLight}" stroke-width="9" fill="none" stroke-linecap="round"/>
  <ellipse cx="10" cy="-82" rx="100" ry="44" fill="${C.lionLight}"/><ellipse cx="10" cy="-82" rx="100" ry="44" fill="url(#shade)"/>
  <circle cx="-10" cy="-90" r="5" fill="${C.lionDark}" opacity=".35"/><circle cx="30" cy="-80" r="5" fill="${C.lionDark}" opacity=".35"/><circle cx="55" cy="-96" r="4" fill="${C.lionDark}" opacity=".35"/>
  <rect x="-80" y="-66" width="24" height="68" rx="11" fill="${C.lionLight}"/><rect x="64" y="-66" width="24" height="68" rx="11" fill="${C.lionLight}"/>
  <circle cx="-88" cy="-128" r="52" fill="${C.lionLight}"/>
  <circle cx="-62" cy="-172" r="17" fill="${C.lionLight}"/><circle cx="-62" cy="-172" r="9" fill="${C.lionDark}"/>
  <circle cx="-118" cy="-170" r="15" fill="${C.lionLight}"/><circle cx="-118" cy="-170" r="8" fill="${C.lionDark}"/>
  <ellipse cx="-112" cy="-106" rx="28" ry="20" fill="#F8E2B4"/>
  <path d="M-128 -118 L-112 -118 L-120 -108 Z" fill="${C.ink}"/>
  <path d="M-120 -108 L-120 -100 Q-128 -92 -134 -98 M-120 -100 Q-112 -92 -106 -98" stroke="${C.ink}" stroke-width="2.5" fill="none" stroke-linecap="round"/>
  <circle cx="-100" cy="-138" r="10" fill="#fff"/><circle cx="-102" cy="-137" r="7" fill="#4a2f10"/><circle cx="-104" cy="-140" r="2.5" fill="#fff"/>
  <circle cx="-138" cy="-138" r="8" fill="#fff"/><circle cx="-139" cy="-137" r="6" fill="#4a2f10"/><circle cx="-141" cy="-139" r="2" fill="#fff"/>`;
}
function lioness() {
  return lion({ mane: false, color: '#D6A34E' });
}
function elephant({ s = 1 } = {}) {
  return `${shadow(260, .3)}
  <rect x="-150" y="-170" width="62" height="172" rx="26" fill="${C.eleDark}"/><rect x="120" y="-170" width="62" height="172" rx="26" fill="${C.eleDark}"/>
  <path d="M200 -260 C235 -230 240 -170 228 -130" stroke="${C.eleDark}" stroke-width="10" fill="none" stroke-linecap="round"/>
  <path d="M-190 -250 C-190 -380 -40 -420 90 -410 C210 -400 240 -320 230 -240 C220 -170 160 -150 40 -150 C-80 -150 -190 -140 -190 -250 Z" fill="${C.ele}"/>
  <path d="M-190 -250 C-190 -380 -40 -420 90 -410 C210 -400 240 -320 230 -240 C220 -170 160 -150 40 -150 C-80 -150 -190 -140 -190 -250 Z" fill="url(#shade)"/>
  <ellipse cx="20" cy="-360" rx="170" ry="40" fill="url(#sheen)"/>
  <rect x="-170" y="-180" width="64" height="182" rx="26" fill="${C.ele}"/><rect x="100" y="-180" width="64" height="182" rx="26" fill="${C.ele}"/>
  <path d="M-170 -8 h64 M100 -8 h64" stroke="${C.eleLight}" stroke-width="6"/>
  <!-- tête -->
  <ellipse cx="-230" cy="-320" rx="90" ry="95" fill="${C.ele}"/>
  <path d="M-300 -300 C-320 -230 -310 -150 -330 -90 C-336 -66 -318 -58 -306 -76 C-286 -140 -270 -220 -250 -270 Z" fill="${C.ele}"/>
  <path d="M-312 -110 l-14 6 M-306 -140 l-14 5 M-300 -170 l-14 4 M-296 -200 l-13 4" stroke="${C.eleDark}" stroke-width="3" stroke-linecap="round"/>
  <path d="M-268 -255 C-290 -240 -310 -230 -330 -240" stroke="#F4EEDF" stroke-width="14" fill="none" stroke-linecap="round"/>
  <path d="M-200 -390 C-110 -420 -70 -330 -90 -260 C-110 -200 -160 -210 -190 -240 C-210 -270 -230 -360 -200 -390 Z" fill="${C.eleDark}"/>
  <path d="M-196 -372 C-130 -392 -104 -330 -116 -280 C-128 -240 -160 -246 -180 -262" fill="${C.eleLight}" opacity=".35"/>
  <circle cx="-262" cy="-338" r="7" fill="#2a2a2a"/><circle cx="-264" cy="-340" r="2.2" fill="#fff"/>
  <path d="M-276 -352 Q-262 -360 -250 -352" stroke="${C.eleDark}" stroke-width="3" fill="none"/>`;
}
function giraffe() {
  const spots = [[-60,-430],[0,-450],[60,-430],[-30,-390],[40,-395],[90,-460],[-90,-460],[10,-500],[-50,-500],[110,-415],
    [-205,-560],[-230,-640],[-250,-720],[-215,-610],[-238,-690]].map(([x,y],i)=>`<path d="M${x-18} ${y-12} l22 -8 l18 10 l-4 20 l-22 6 l-16 -10 Z" fill="${C.girSpot}" transform="rotate(${i*23} ${x} ${y})"/>`).join('');
  return `${shadow(170)}
  <path d="M-80 -400 L-96 -2 M-50 -400 L-60 -2" stroke="#C99A4E" stroke-width="22" stroke-linecap="round"/>
  <path d="M100 -400 L130 -2 M70 -410 L82 -2" stroke="#C99A4E" stroke-width="22" stroke-linecap="round"/>
  <path d="M130 -460 C170 -440 170 -380 182 -330" stroke="${C.gir}" stroke-width="8" fill="none"/><ellipse cx="184" cy="-322" rx="8" ry="14" fill="${C.ink}"/>
  <path d="M-120 -440 C-120 -510 -40 -520 60 -510 C140 -500 150 -450 140 -410 C130 -380 60 -380 0 -385 C-80 -388 -120 -390 -120 -440 Z" fill="${C.gir}"/>
  <path d="M-110 -470 L-200 -780 L-160 -800 L-40 -500 Z" fill="${C.gir}"/>
  <path d="M-196 -790 L-180 -720 M-170 -800 L-120 -620" stroke="${C.girSpot}" stroke-width="10" stroke-linecap="round" opacity=".55"/>
  ${spots}
  <path d="M-120 -440 C-120 -510 -40 -520 60 -510 C140 -500 150 -450 140 -410 C130 -380 60 -380 0 -385 C-80 -388 -120 -390 -120 -440 Z" fill="url(#shade)"/>
  <path d="M-100 -400 L-110 -2 M80 -400 L100 -2" stroke="${C.gir}" stroke-width="24" stroke-linecap="round"/>
  <path d="M-110 -16 h22 M100 -16 h22" stroke="${C.ink}" stroke-width="12" stroke-linecap="round"/>
  <!-- tête -->
  <path d="M-170 -820 C-200 -840 -250 -830 -280 -800 C-300 -780 -290 -760 -262 -760 C-230 -760 -190 -770 -160 -780 Z" fill="${C.gir}"/>
  <ellipse cx="-282" cy="-786" rx="18" ry="16" fill="#D7A456"/>
  <path d="M-210 -830 L-214 -870 M-190 -826 L-190 -866" stroke="${C.girSpot}" stroke-width="8" stroke-linecap="round"/><circle cx="-214" cy="-872" r="8" fill="${C.ink}"/><circle cx="-190" cy="-868" r="8" fill="${C.ink}"/>
  <ellipse cx="-168" cy="-830" rx="22" ry="9" fill="${C.gir}" transform="rotate(-25 -168 -830)"/>
  <circle cx="-222" cy="-806" r="6.5" fill="#2a1a0a"/><circle cx="-224" cy="-808" r="2" fill="#fff"/>
  <path d="M-160 -800 C-140 -760 -130 -720 -120 -640" stroke="#8A5524" stroke-width="10" fill="none" stroke-linecap="round"/>`;
}
function monkey() {
  return `${shadow(80)}
  <path d="M40 -40 C120 -40 120 -160 70 -170 C40 -176 40 -140 64 -140" stroke="${C.monkey}" stroke-width="12" fill="none" stroke-linecap="round"/>
  <ellipse cx="0" cy="-70" rx="56" ry="70" fill="${C.monkey}"/>
  <ellipse cx="-6" cy="-60" rx="32" ry="44" fill="${C.monkeyFace}" opacity=".85"/>
  <path d="M-40 -20 Q-30 4 -6 2 M40 -20 Q30 4 6 2" stroke="${C.monkey}" stroke-width="20" fill="none" stroke-linecap="round"/>
  <path d="M-46 -100 Q-90 -60 -56 -30" stroke="${C.monkey}" stroke-width="16" fill="none" stroke-linecap="round"/>
  <path d="M46 -100 Q90 -70 70 -40" stroke="${C.monkey}" stroke-width="16" fill="none" stroke-linecap="round"/>
  <circle cx="0" cy="-170" r="52" fill="${C.monkey}"/>
  <circle cx="-54" cy="-172" r="18" fill="${C.monkey}"/><circle cx="-54" cy="-172" r="10" fill="${C.monkeyFace}"/>
  <circle cx="54" cy="-172" r="18" fill="${C.monkey}"/><circle cx="54" cy="-172" r="10" fill="${C.monkeyFace}"/>
  <path d="M-34 -176 C-34 -206 -2 -208 0 -186 C2 -208 34 -206 34 -176 C34 -140 -34 -140 -34 -176 Z" fill="${C.monkeyFace}"/>
  <circle cx="-14" cy="-182" r="6" fill="#2a1a0a"/><circle cx="14" cy="-182" r="6" fill="#2a1a0a"/><circle cx="-16" cy="-184" r="2" fill="#fff"/><circle cx="12" cy="-184" r="2" fill="#fff"/>
  <path d="M-4 -166 h8" stroke="#6b4a2f" stroke-width="3" stroke-linecap="round"/>
  <path d="M-14 -154 Q0 -144 14 -154" stroke="#6b4a2f" stroke-width="3" fill="none" stroke-linecap="round"/>`;
}
function crocodile() {
  const scutes = Array.from({ length: 14 }, (_, i) => `<path d="M${-130 + i * 26} -66 l10 -16 l10 16 Z" fill="${C.crocDark}"/>`).join('');
  return `${shadow(260, .25)}
  <path d="M-110 -40 l-18 40 h26 l10 -36 M90 -40 l14 40 h26 l-12 -40" fill="${C.crocDark}"/>
  <path d="M-330 -40 C-300 -64 -220 -66 -160 -70 C-60 -78 60 -76 160 -62 C230 -52 300 -40 380 -24 C300 -20 220 -16 150 -14 C40 -10 -60 -12 -160 -16 C-230 -18 -300 -20 -330 -40 Z" fill="${C.croc}"/>
  <path d="M-330 -40 C-300 -30 -230 -24 -160 -20 L-160 -16 C-230 -18 -300 -20 -330 -40 Z" fill="#C9C48A"/>
  ${scutes}
  <path d="M-150 -30 l-20 30 h28 l12 -28 M60 -30 l10 30 h28 l-8 -30" fill="${C.croc}"/>
  <ellipse cx="-180" cy="-76" rx="16" ry="12" fill="${C.croc}"/><circle cx="-184" cy="-80" r="6" fill="#E8C75A"/><circle cx="-185" cy="-80" r="3" fill="${C.ink}"/>
  <path d="M-322 -38 L-170 -32" stroke="${C.crocDark}" stroke-width="3"/>
  <path d="M-300 -36 l4 6 l4 -6 M-270 -35 l4 6 l4 -6 M-240 -34 l4 6 l4 -6" stroke="#fff" stroke-width="2.5" fill="none"/>`;
}
function cheetah() {
  const spots = Array.from({ length: 26 }, (_, i) => `<circle cx="${-120 + (i * 37) % 270}" cy="${-150 + ((i * 53) % 60)}" r="6" fill="${C.ink}" opacity=".8"/>`).join('');
  return `${shadow(220)}
  <path d="M150 -150 C230 -170 280 -120 320 -150" stroke="${C.cheetah}" stroke-width="12" fill="none" stroke-linecap="round"/>
  <path d="M290 -142 l10 -4 M305 -148 l10 -2" stroke="${C.ink}" stroke-width="10"/>
  <path d="M-110 -120 L-230 -60 M-90 -120 L-160 -30" stroke="#C99A40" stroke-width="18" stroke-linecap="round"/>
  <path d="M120 -120 L230 -40 M100 -110 L150 -20" stroke="#C99A40" stroke-width="18" stroke-linecap="round"/>
  <path d="M-150 -150 C-140 -200 -40 -200 60 -190 C140 -185 170 -160 160 -130 C150 -105 60 -110 -20 -112 C-100 -114 -158 -116 -150 -150 Z" fill="${C.cheetah}"/>
  ${spots}
  <path d="M-130 -130 L-260 -110 M140 -130 L250 -80" stroke="${C.cheetah}" stroke-width="18" stroke-linecap="round"/>
  <circle cx="-180" cy="-185" r="38" fill="${C.cheetah}"/>
  <circle cx="-168" cy="-218" r="10" fill="${C.cheetah}"/>
  <path d="M-196 -196 C-200 -176 -206 -160 -210 -150" stroke="${C.ink}" stroke-width="4" fill="none"/>
  <circle cx="-194" cy="-198" r="5" fill="#2a1a0a"/><path d="M-218 -178 l8 0 l-4 6 Z" fill="${C.ink}"/>`;
}
function zebra() {
  const stripes = Array.from({ length: 9 }, (_, i) => `<path d="M${-110 + i * 26} -240 q8 40 -4 80" stroke="${C.ink}" stroke-width="9" fill="none"/>`).join('');
  return `${shadow(160)}
  <rect x="-100" y="-160" width="22" height="160" rx="10" fill="#e9e9e9"/><rect x="90" y="-160" width="22" height="160" rx="10" fill="#e9e9e9"/>
  <path d="M-130 -200 C-130 -250 -40 -258 60 -252 C140 -248 150 -210 140 -180 C130 -160 60 -160 0 -162 C-80 -164 -130 -165 -130 -200 Z" fill="#F6F4EE"/>
  ${stripes}
  <path d="M-120 -220 L-200 -330 L-170 -350 L-80 -240 Z" fill="#F6F4EE"/>
  <path d="M-150 -290 l30 -6 M-170 -315 l26 -8 M-130 -262 l28 -6" stroke="${C.ink}" stroke-width="8"/>
  <path d="M-200 -340 L-160 -360 L-170 -330" fill="${C.ink}"/>
  <ellipse cx="-220" cy="-320" rx="34" ry="20" fill="#F6F4EE" transform="rotate(30 -220 -320)"/>
  <ellipse cx="-240" cy="-306" rx="12" ry="10" fill="${C.ink}"/>
  <rect x="-120" y="-170" width="22" height="170" rx="10" fill="#F6F4EE"/><rect x="70" y="-170" width="22" height="170" rx="10" fill="#F6F4EE"/>
  <path d="M-120 -40 h22 M-120 -70 h22 M70 -40 h22 M70 -70 h22" stroke="${C.ink}" stroke-width="7"/>`;
}
function child() {
  return `${shadow(60)}
  <rect x="-34" y="-110" width="26" height="110" rx="12" fill="#1B2A4A"/><rect x="6" y="-110" width="26" height="110" rx="12" fill="#1B2A4A"/>
  <path d="M-46 -240 Q0 -260 46 -240 L42 -100 L-42 -100 Z" fill="#2F5D3A"/>
  <path d="M-40 -220 Q-90 -180 -150 -200" stroke="#2F5D3A" stroke-width="24" fill="none" stroke-linecap="round"/>
  <circle cx="-156" cy="-202" r="14" fill="#C68E5E"/>
  <path d="M40 -220 Q56 -170 40 -130" stroke="#2F5D3A" stroke-width="22" fill="none" stroke-linecap="round"/>
  <circle cx="0" cy="-292" r="46" fill="#C68E5E"/>
  <path d="M-46 -300 C-46 -350 46 -350 46 -300 C30 -320 -20 -322 -46 -300 Z" fill="#2A1A10"/>
  <circle cx="-16" cy="-292" r="5" fill="#2a1a0a"/><circle cx="14" cy="-292" r="5" fill="#2a1a0a"/>
  <path d="M-12 -270 Q0 -262 12 -270" stroke="#7a4a2a" stroke-width="3" fill="none" stroke-linecap="round"/>`;
}
function bubble(x, y, w, h, tailX, tailY) {
  return `<path d="M${x} ${y + 30} Q${x} ${y} ${x + 30} ${y} L${x + w - 30} ${y} Q${x + w} ${y} ${x + w} ${y + 30} L${x + w} ${y + h - 30} Q${x + w} ${y + h} ${x + w - 30} ${y + h} L${x + w * .45} ${y + h} L${tailX} ${tailY} L${x + w * .3} ${y + h} L${x + 30} ${y + h} Q${x} ${y + h} ${x} ${y + h - 30} Z" fill="#fff" stroke="#1B2A4A" stroke-width="5"/>`;
}
function tree(x, y, s = 1) {
  return g(x, y, s, `<rect x="-14" y="-160" width="28" height="160" rx="8" fill="#6B4E32"/><circle cx="0" cy="-200" r="80" fill="#3F6B3A"/><circle cx="-50" cy="-170" r="55" fill="#4C7A44"/><circle cx="50" cy="-170" r="58" fill="#36603A"/><circle cx="-10" cy="-240" r="50" fill="#5A8A4E"/>`);
}
function banana(x, y, s = 1) {
  return g(x, y, s, `<path d="M0 0 C20 -40 60 -50 90 -40 C60 -30 30 -10 10 10 Z" fill="#F2D04A" stroke="#C9A22A" stroke-width="3"/>`);
}

module.exports = { C, defs, g, savanna, acacia, grassTufts, lion, cub, lioness, elephant, giraffe, monkey, crocodile, cheetah, zebra, child, bubble, tree, banana, shadow };
