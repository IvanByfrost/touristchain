"""Genera los diagramas de documentación de Asgard/TouristChain en PNG (PIL)."""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "diagramas")
os.makedirs(OUT, exist_ok=True)

NAVY = (26, 42, 108)
GOLD = (255, 214, 10)
DARK = (17, 17, 17)
GRAY = (240, 242, 245)
WHITE = (255, 255, 255)
BLUE = (74, 111, 255)

def font(size, bold=False):
    path = "C:/Windows/Fonts/" + ("arialbd.ttf" if bold else "arial.ttf")
    try:
        return ImageFont.truetype(path, size)
    except OSError:
        return ImageFont.load_default()

def new_canvas(w=1600, h=1000, title=""):
    img = Image.new("RGB", (w, h), WHITE)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, w, 110], fill=NAVY)
    d.text((60, 28), title, font=font(44, True), fill=GOLD)
    d.text((60, 72), "ASGARD  ·  Plataforma TouristChain", font=font(22), fill=WHITE)
    return img, d

def box(d, xy, text, fill=WHITE, outline=NAVY, tfill=DARK, fsize=24, bold=False, width=3):
    x0, y0, x1, y1 = xy
    d.rectangle(xy, fill=fill, outline=outline, width=width)
    f = font(fsize, bold)
    bbox = d.multiline_textbbox((0, 0), text, font=f)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    d.multiline_text(((x0 + x1 - tw) / 2, (y0 + y1 - th) / 2), text, font=f, fill=tfill, align="center")

def arrow(d, x0, y0, x1, y1, color=NAVY, width=3):
    d.line([x0, y0, x1, y1], fill=color, width=width)
    import math
    ang = math.atan2(y1 - y0, x1 - x0)
    s = 14
    for da in (2.6, -2.6):
        d.line([x1, y1, x1 + s * math.cos(ang + da), y1 + s * math.sin(ang + da)], fill=color, width=width)

def v_arrow(d, x, y0, y1, color=NAVY):
    arrow(d, x, y0, x, y1, color)

def h_arrow(d, x0, x1, y, color=NAVY):
    arrow(d, x0, y, x1, y, color)

def label(d, x, y, text, size=22, color=NAVY):
    d.text((x, y), text, font=font(size, True), fill=color)

# ---------- 1. ARQUITECTURA ----------
img, d = new_canvas(title="1. Arquitectura del sistema")
label(d, 60, 140, "CLIENTE (navegador · Vite)")
box(d, (60, 180, 500, 330), "index.html\nPortal del viajero\nviajes · pagos · dashboard", fsize=25)
box(d, (60, 350, 500, 470), "admin.html\nPanel administración", fsize=25)
box(d, (60, 490, 500, 610), "socio.html\nPanel del socio", fsize=25)
box(d, (60, 630, 500, 740), "SCSS + Leaflet + QR\nCDN externos", fill=GRAY, fsize=25)
for y in (255, 410, 550):
    h_arrow(d, 500, 640, y)
label(d, 660, 140, "BACKEND (Node + Express :3001)")
routers = "auth · users · trips · hotels\ncars · bookings · payments (QR)\npartners (socios/NIT) · reviews\nitineraries · budgets\nnotifications · chat · companies"
box(d, (660, 180, 1120, 430), "API REST + JWT\n" + routers, fsize=24)
box(d, (660, 450, 1120, 570), "db.json (JSON)\nusuarios · reservas · pagos", fill=GOLD, outline=NAVY, tfill=NAVY, fsize=24)
v_arrow(d, 890, 430, 450)
h_arrow(d, 1120, 1240, 300)
label(d, 1260, 140, "ROLES")
box(d, (1260, 180, 1540, 300), "Viajero", fill=BLUE, tfill=WHITE, fsize=26, bold=True)
box(d, (1260, 320, 1540, 440), "Socio (NIT)", fill=BLUE, tfill=WHITE, fsize=26, bold=True)
box(d, (1260, 460, 1540, 580), "Admin", fill=BLUE, tfill=WHITE, fsize=26, bold=True)
d.text((60, 920), "JWT en cada petición · rate-limit en login · QR con host dinámico", font=font(22), fill=DARK)
img.save(f"{OUT}/01-arquitectura.png")

# ---------- 2. CASOS DE USO ----------
img, d = new_canvas(title="2. Casos de uso por rol")
label(d, 60, 140, "VIAJERO")
box(d, (60, 180, 500, 640),
    "· Registrarse / entrar\n· Explorar viajes y mapa\n· Ver foto 360\n· Reservar\n· Pagar (tarjeta/QR/Nequi)\n· Itinerarios y presupuestos\n· Reseñas y favoritos\n· Recuperar contraseña", fsize=24)
label(d, 580, 140, "SOCIO (empresa con NIT)")
box(d, (580, 180, 1020, 640),
    "· Entrar con cuenta creada\n  por el admin\n· Ver resumen\n· Editar datos empresa\n  (NIT solo lectura)\n· Publicar vitrina\n· Ver reservas y avisos\n· Cambiar contraseña", fsize=24)
label(d, 1100, 140, "ADMINISTRADOR")
box(d, (1100, 180, 1540, 640),
    "· Ver estadísticas\n· Gestionar usuarios\n· Crear socios (NIT)\n· Gestionar empresas\n· Ver pagos y viajes\n· Reportes y ajustes", fsize=24)
img.save(f"{OUT}/02-casos-uso.png")

# ---------- 3. FLUJO DE PAGO ----------
img, d = new_canvas(h=1050, title="3. Flujo de pago (simulado)")
box(d, (600, 150, 1000, 230), "1. Reservar viaje/hotel/carro", fill=NAVY, tfill=WHITE, fsize=26, bold=True)
v_arrow(d, 800, 230, 260)
box(d, (600, 260, 1000, 340), "2. Mis Reservas > Pagar", fsize=25)
v_arrow(d, 800, 340, 370)
box(d, (600, 370, 1000, 450), "3. Elegir método", fsize=25)
for x in (300, 800, 1300):
    v_arrow(d, x, 450, 480)
box(d, (60, 480, 540, 660), "TARJETA\nvalidar número/MM-AA/CVC\n> aprobación inmediata", fill=GRAY, fsize=24)
box(d, (560, 480, 1040, 660), "NEQUI\nteléfono 10 dígitos\n> push simulado 2 s\n> aprobación", fill=GRAY, fsize=24)
box(d, (1060, 480, 1540, 660), "QR\nse genera imagen real\n> escaneo con celular\n> polling cada 3 s", fill=GRAY, fsize=24)
for x in (300, 800, 1300):
    v_arrow(d, x, 660, 700)
box(d, (600, 700, 1000, 790), "4. Marcar pagado + ticket con QR", fill=NAVY, tfill=WHITE, fsize=25, bold=True)
v_arrow(d, 800, 790, 820)
box(d, (600, 820, 1000, 910), "5. Historial y estadísticas", fsize=25)
img.save(f"{OUT}/03-flujo-pago.png")

# ---------- 4. FLUJO SOCIO ----------
img, d = new_canvas(title="4. Flujo socio (NIT)")
box(d, (60, 180, 500, 300), "1. Admin crea socio\nempresa + NIT + contacto\nemail + contraseña", fill=NAVY, tfill=WHITE, fsize=24, bold=True)
h_arrow(d, 500, 580, 240)
box(d, (580, 180, 1020, 300), "2. Cuenta socio activa\nrol=socio · NIT único\nvitrina publicada", fsize=24)
h_arrow(d, 1020, 1100, 240)
box(d, (1100, 180, 1540, 300), "3. Socio ingresa\nlogin > socio.html\n(solo rol socio)", fsize=24)
v_arrow(d, 1320, 300, 360)
box(d, (1100, 360, 1540, 560), "4. Panel del socio\nresumen · mi empresa\nmi vitrina · reservas\navisos · contraseña", fill=GOLD, outline=NAVY, tfill=NAVY, fsize=24)
h_arrow(d, 1100, 1020, 460, color=NAVY)
box(d, (580, 360, 1020, 560), "5. Vitrina visible\nen Socios del sitio\npara los viajeros", fsize=24)
d.text((60, 920), "El NIT solo lo edita el admin · email y NIT no se repiten", font=font(22), fill=DARK)
img.save(f"{OUT}/04-flujo-socio.png")

# ---------- 5. MODELO DE DATOS ----------
img, d = new_canvas(h=1050, title="5. Modelo de datos (db.json)")
box(d, (60, 160, 470, 400),
    "USERS\n—\nid · name · email\npassword (hash) · role\n(user/socio/admin)\nstatus · phone · avatar\npartnerProfile:\ncompany · NIT · contacto\ntel · dirección · ciudad", fsize=21)
box(d, (560, 160, 960, 340),
    "PARTNERS (vitrina)\n—\nuserId > users\nname · type · description\nlogo · website · benefits", fsize=22)
box(d, (1050, 160, 1540, 340),
    "BOOKINGS\n—\nuserId > users\nitemName · itemType\ntotal · status", fsize=22)
box(d, (60, 470, 470, 640),
    "PAYMENTS\n—\nuserId · amount\nmethod (tarjeta/QR/Nequi)\nstatus · reference\nqrToken · reservationIds", fsize=21)
box(d, (560, 470, 960, 640),
    "CATÁLOGO\n—\ntrips · hotels · cars\nprecio · imagen\ncoordenadas", fsize=22)
box(d, (1050, 470, 1540, 640),
    "USUARIO\n—\nreviews · itineraries\nbudgets · notifications\nfavorites (local)", fsize=22)
box(d, (60, 700, 1540, 800), "Relaciones:  users 1—N bookings  ·  payments N—1 bookings (reservationIds)  ·  users 1—1 partnerProfile  ·  partners.userId > users.id", fill=NAVY, tfill=WHITE, fsize=23, bold=True)
d.text((60, 950), "Resets de contraseña: colección resets { email · code · expires 15 min }", font=font(22), fill=DARK)
img.save(f"{OUT}/05-modelo-datos.png")

print("Diagramas generados en", OUT)
