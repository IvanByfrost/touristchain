# 8. Seguridad, correcciones y limitaciones

## Correcciones aplicadas (auditoría)

1. **`/api/users` y `/api/companies` exigían nada:** ahora solo admin; las
   respuestas ya no incluyen hashes de contraseña.
2. **Usuarios creados por el admin no podían entrar** (clave en plano): ahora
   se cifra con bcrypt al crear/editar.
3. **JWT quemado:** ahora sale de `JWT_SECRET` (con aviso si falta).
4. **Login sin freno:** rate-limit de 8 intentos / 10 min por email+IP.
5. **"Olvidé contraseña" falso:** ahora flujo real con código de 6 dígitos
   (15 min) y restablecimiento efectivo.
6. **XSS en renders:** escapado `esc()` en tarjetas, reseñas, itinerarios,
   presupuestos, notificaciones, socios, reservas e historial.
7. **Bug favoritos:** el botón eliminar se ejecutaba solo al renderizar;
   ahora solo con clic. Más botones en estados vacíos.

## Limitaciones del prototipo

- Pagos y correo de recuperación **simulados** (sin pasarela ni SMTP real).
- Base **JSON en archivo** (sin concurrencia transaccional).
- QR apunta al host de la demo (red local, no internet).
- Sin HTTPS, backups ni monitoreo: pendientes para producción.
