import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

router.get('/', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    let items = db.budgets || [];
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
    const { name, destination, travelers, days, flights, hotel, food, activities, transport } = req.body;
    if (!name || !destination) {
      return res.status(400).json({ error: 'Nombre y destino son requeridos' });
    }

    const db = readDb();
    const items = db.budgets || [];

    const total = (flights || 0) + (hotel || 0) + (food || 0) + (activities || 0) + (transport || 0);

    const newItem = {
      id: generateId(items),
      userId: req.user.id,
      name,
      destination,
      travelers: parseInt(travelers) || 1,
      days: parseInt(days) || 1,
      flights: parseFloat(flights) || 0,
      hotel: parseFloat(hotel) || 0,
      food: parseFloat(food) || 0,
      activities: parseFloat(activities) || 0,
      transport: parseFloat(transport) || 0,
      total,
      createdAt: new Date().toISOString()
    };

    items.push(newItem);
    db.budgets = items;
    writeDb(db);

    res.status(201).json(newItem);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const items = db.budgets || [];
    const index = items.findIndex(i => i.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Presupuesto no encontrado' });
    if (req.user.role !== 'admin' && items[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    const deleted = items.splice(index, 1)[0];
    db.budgets = items;
    writeDb(db);

    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
