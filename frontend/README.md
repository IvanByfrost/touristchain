# ASGARD · Plataforma TouristChain

Plataforma web de turismo: exploración de destinos, reservas, pagos simulados
(tarjeta, QR escaneable y Nequi) y tres portales por rol
(**viajero**, **socio** con NIT y **administrador**).

![Arquitectura](docs/diagramas/01-arquitectura.png)

## Demo rápida

| Portal | URL (dev) | Credenciales de prueba |
|---|---|---|
| Viajero | http://localhost:5173/ | `juan@example.com` / `admin123` |
| Socio | http://localhost:5173/socio.html | `socio@caribereal.com` / `socio1234` |
| Admin | http://localhost:5173/admin.html | `admin@touristchain.com` / `admin123` |

> Los pagos son **simulados**: no se cobra dinero real.

## Instalación en 3 pasos

```bash
npm install
npm run server   # backend :3001 (terminal 1)
npm run dev      # frontend :5173 (terminal 2)
```

Ver [instalación](docs/06-instalacion.md), [manual de usuario](docs/03-manual-usuario.md)
y [documentación completa](docs/00-portada.md).
