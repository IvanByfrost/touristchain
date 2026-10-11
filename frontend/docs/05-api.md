# 5. API del backend

Auth JWT (`Authorization: Bearer <token>`). Roles: `user`, `socio`, `admin`.
El token lo emite el backend Java y el mock Node lo valida con el mismo `JWT_SECRET`.

## Backend Java (`http://localhost:8080/api/v1`)

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | /auth/register | — | Crear cuenta viajero |
| POST | /auth/login | — | Entrar (bloqueo tras 5 intentos fallidos) |
| GET | /auth/me | token | Mi perfil |
| PUT | /auth/profile | token | Editar perfil |
| PATCH | /auth/password | token | Cambiar clave |
| GET | /partners | — | Vitrina pública (solo socios activos) |
| GET/PUT | /partners/me | socio | Mi perfil / editar empresa (sin NIT) |
| POST | /partners/accounts | admin | **Crear socio (NIT)**: usuario de ingreso + empresa |
| PUT | /partners/accounts/:userId | admin | Editar NIT, datos y estado del socio |
| GET/POST | /users | admin | Listar / crear usuarios (`user` o `admin`) |
| PUT | /users/:id | admin | Editar datos, rol, estado o contraseña |
| DELETE | /users/:id | admin | Deshabilitar cuenta (deja de aparecer y no puede entrar) |
| GET | /reviews?itemType=&itemId= | — | Reseñas públicas |
| GET | /reviews/stats?itemType=&itemId= | — | Promedio y cantidad |
| GET | /reviews/me | token | Mis reseñas |
| POST | /reviews | turista | Publicar (una por ítem) |
| PUT/DELETE | /reviews/:id | autor* | Editar / eliminar |

*El administrador también puede eliminar cualquier reseña.

Estados de cuenta que usa el panel: `activo`, `pendiente`, `bloqueado`, `inactivo`.
Un administrador no puede cambiar el estado ni eliminar su propia cuenta.

## Mock Node (`http://localhost:3001/api`)

Pendiente de migrar a Java.

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| GET | /trips, /hotels, /cars | — | Catálogo público |
| POST/PUT/DELETE | /trips, /hotels, /cars | admin | Gestionar catálogo |
| GET/POST | /bookings | token | Mis reservas / reservar |
| GET/POST | /payments | token | Historial / pagar tarjeta-Nequi |
| POST | /payments/qr | token | Generar QR real |
| GET | /payments/qr/:token/confirm | — | Escaneo del QR (aprueba) |
| GET | /payments/:id/status | token | Sondeo del QR |
| GET | /companies | admin | Empresas |
| GET/POST | /itineraries, /budgets, /notifications | token | Contenido del usuario |
| POST | /chat/ask | — | Asistente virtual |
