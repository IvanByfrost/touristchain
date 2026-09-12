# 4. Manual de administrador y socio

## Administrador (`admin.html`, `admin@touristchain.com` / `admin123`)

- **Dashboard:** usuarios, ingresos del día, viajes activos, pendientes y
  **Socios Activos**, con gráficos y actividad reciente.
- **Usuarios:** crear (la contraseña se cifra y el usuario sí puede entrar),
  editar, ver, eliminar, filtrar y exportar.
- **Socios:** **Nuevo Socio** pide empresa, **NIT**, contacto, email, contraseña,
  teléfono, dirección, ciudad y tipo. NIT y email únicos. Luego permite
  **editar NIT y datos**, activar/desactivar y eliminar.
- **Empresas, Pagos, Viajes, Reportes, Configuración:** CRUD completo.

## Socio (`socio.html`, `socio@caribereal.com` / `socio1234`)

La cuenta la crea el admin; el socio entra desde el login principal y cae a
su panel:

1. **Resumen:** estado, vitrina publicada, destinos y reservas + datos empresa.
2. **Mi Empresa:** edita todo menos el **NIT** (solo lectura; lo cambia el admin).
3. **Mi Vitrina:** descripción, web, logo y beneficios → se publica en
   **Socios** del sitio principal.
4. **Mis Reservas, Notificaciones** (marcar leídas) y **Configuración**
   (cambio de contraseña).
