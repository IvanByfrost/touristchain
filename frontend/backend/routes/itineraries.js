import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    let items = db.itineraries || [];
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
    const { name, destination, days } = req.body;
    if (!name || !destination || !Array.isArray(days) || days.length === 0) {
      return res.status(400).json({ error: 'Nombre, destino y días son requeridos' });
    }

    const db = readDb();
    const items = db.itineraries || [];

    const newItem = {
      id: generateId(items),
      userId: req.user.id,
      name,
      destination,
      days,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    items.push(newItem);
    db.itineraries = items;
    writeDb(db);

    res.status(201).json(newItem);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.put('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.itineraries || [];
    const index = items.findIndex(i => i.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Itinerario no encontrado' });
    if (req.user.role !== 'admin' && items[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    items[index] = { ...items[index], ...req.body, id: items[index].id, updatedAt: new Date().toISOString() };
    db.itineraries = items;
    writeDb(db);

    res.json(items[index]);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.itineraries || [];
    const index = items.findIndex(i => i.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Itinerario no encontrado' });
    if (req.user.role !== 'admin' && items[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    const deleted = items.splice(index, 1)[0];
    db.itineraries = items;
    writeDb(db);

    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
