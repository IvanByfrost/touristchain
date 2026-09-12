import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    let items = db.notifications || [];
    if (req.user.role !== 'admin') {
      items = items.filter(i => i.userId === req.user.id);
    }
    res.json(items.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/', authenticateToken, (req, res) => {
  try {
    const { title, message, type = 'info', userId } = req.body;
    if (!title || !message) {
      return res.status(400).json({ error: 'Título y mensaje son requeridos' });
    }

    const db = readDb();
    const items = db.notifications || [];

    const newItem = {
      id: generateId(items),
      userId: userId || req.user.id,
      title,
      message,
      type,
      read: false,
      createdAt: new Date().toISOString()
    };

    items.push(newItem);
    db.notifications = items;
    writeDb(db);

    res.status(201).json(newItem);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.patch('/:id/read', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.notifications || [];
    const index = items.findIndex(i => i.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Notificación no encontrada' });
    if (req.user.role !== 'admin' && items[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    items[index].read = true;
    db.notifications = items;
    writeDb(db);

    res.json(items[index]);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.patch('/read-all', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.notifications || [];

    items.forEach(item => {
      if (req.user.role === 'admin' || item.userId === req.user.id) {
        item.read = true;
      }
    });

    db.notifications = items;
    writeDb(db);

    res.json({ message: 'Notificaciones marcadas como leídas' });
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.notifications || [];
    const index = items.findIndex(i => i.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Notificación no encontrada' });
    if (req.user.role !== 'admin' && items[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    const deleted = items.splice(index, 1)[0];
    db.notifications = items;
    writeDb(db);

    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
