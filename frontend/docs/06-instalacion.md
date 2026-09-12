# 6. Instalación y despliegue

## Requisitos

Node.js 18+, npm, navegador moderno. Puertos libres **3001** (API) y
**5173** (frontend dev).

## Desarrollo

```bash
npm install
npm run server   # backend :3001 (terminal 1)
npm run dev      # frontend :5173 (terminal 2)
```

## Producción

```bash
npm run build    # genera dist/ (index, admin, socio)
npm start        # build + servidor que sirve el frontend
```

## Variables de entorno (`.env`, ver `.env.example`)

| Variable | Defecto | Descripción |
|---|---|---|
| JWT_SECRET | temporal | Secreto JWT (**definirlo en producción**) |
| PORT | 3001 | Puerto del backend |

## Estructura

- `index.html`, `admin.html`, `socio.html` + `vite.config.js`.
- `src/js/`: `main.js` (viajero), `admin.js`, `socio.js`, `api.js` (cliente REST).
- `src/scss/`: `main.scss`, `admin.scss`, `theme.scss`.
- `backend/`: `server.js`, `routes/` (15), `middleware/auth.js`, `utils/db.js`,
  `data/db.json` (ignorada por git; se crea con datos semilla).
- `public/assets/`, `docs/` (esta documentación + diagramas).
