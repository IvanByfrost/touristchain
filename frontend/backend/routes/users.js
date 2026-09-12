import express from 'express';
import bcrypt from 'bcryptjs';
import { createCrudRoutes } from './crud.js';
import { readDb, writeDb } from '../utils/db.js';
import { authenticateToken, requireAdmin } from '../middleware/auth.js';

const router = express.Router();

// Todo el módulo de usuarios es solo para administradores
router.use(authenticateToken, requireAdmin);

// Cifrar contraseña si viene en creación/edición (el CRUD genérico la guardaría en plano)
async function hashPasswordBody(req, res, next) {
  try {
    if (req.body && req.body.password) {
      if (req.body.password.length < 8) {
        return res.status(400).json({ error: 'La contraseña debe tener al menos 8 caracteres' });
      }
      req.body.password = await bcrypt.hash(req.body.password, 10);
    }
    next();
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
}

router.post('/', hashPasswordBody);
router.put('/:id', hashPasswordBody);

// Rutas CRUD básicas (sin el hash de la contraseña en las respuestas)
router.use('/', createCrudRoutes('users', { omit: ['password'] }));

// Endpoint para cambiar estado
router.patch('/:id/status', async (req, res) => {
  try {
    const db = readDb();
    const user = db.users.find(u => u.id === parseInt(req.params.id));

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    user.status = req.body.status || user.status;
    writeDb(db);

    res.json(user);
  } catch (error) {
    console.error('Error al actualizar estado:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// Endpoint para cambiar contraseña
router.patch('/:id/password', async (req, res) => {
  try {
    const { password } = req.body;

    if (!password || password.length < 8) {
      return res.status(400).json({ error: 'Contraseña inválida' });
    }

    const db = readDb();
    const user = db.users.find(u => u.id === parseInt(req.params.id));

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    user.password = await bcrypt.hash(password, 10);
    writeDb(db);

    res.json({ message: 'Contraseña actualizada' });
  } catch (error) {
    console.error('Error al cambiar contraseña:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
