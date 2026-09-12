# 5. API del backend (`http://localhost:3001/api`)

Auth JWT (`Authorization: Bearer <token>`). Roles: `user`, `socio`, `admin`.

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | /auth/register | — | Crear cuenta viajero |
| POST | /auth/login | — | Entrar (rate-limit 8/10 min) |
| GET | /auth/me | token | Mi perfil |
| PUT | /auth/profile | token | Editar perfil/avatar |
| PATCH | /auth/password | token | Cambiar clave |
| POST | /auth/forgot | — | Pedir código (demo lo devuelve) |
| POST | /auth/reset | — | Restablecer con código |
| GET | /trips, /hotels, /cars | — | Catálogo público |
| POST/PUT/DELETE | /trips, /hotels, /cars | admin | Gestionar catálogo |
| GET/POST | /bookings | token | Mis reservas / reservar |
| GET/POST | /payments | token | Historial / pagar tarjeta-Nequi |
| POST | /payments/qr | token | Generar QR real |
| GET | /payments/qr/:token/confirm | — | Escaneo del QR (aprueba) |
| GET | /payments/:id/status | token | Sondeo del QR |
| GET | /partners | — | Vitrina pública |
| POST | /partners/accounts | admin | **Crear socio (NIT)** |
| PUT | /partners/accounts/:id | admin | Editar NIT y datos |
| GET/PUT | /partners/me | socio | Mi perfil / editar empresa |
| GET | /users, /companies | admin | Gestión (sin hashes) |
| GET/POST | /reviews, /itineraries, /budgets, /notifications | token* | Contenido del usuario |
| POST | /chat/ask | — | Asistente virtual |

*reviews lectura pública; escritura con token.
