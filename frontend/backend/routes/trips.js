import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken, requireAdmin } from '../middleware/auth.js';

const router = express.Router();

router.get('/', (req, res) => {
  try {
    const db = readDb();
    let trips = db.trips || [];
    const { destination, minPrice, maxPrice, tag, status } = req.query;

    if (destination) {
      trips = trips.filter(t => t.destination.toLowerCase().includes(destination.toLowerCase()));
    }
    if (minPrice) trips = trips.filter(t => t.price >= parseFloat(minPrice));
    if (maxPrice) trips = trips.filter(t => t.price <= parseFloat(maxPrice));
    if (tag) trips = trips.filter(t => t.tags && t.tags.includes(tag.toLowerCase()));
    if (status) trips = trips.filter(t => t.status === status);

    res.json(trips);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.get('/:id', (req, res) => {
  try {
    const db = readDb();
    const trip = (db.trips || []).find(t => t.id === parseInt(req.params.id));
    if (!trip) return res.status(404).json({ error: 'Viaje no encontrado' });
    res.json(trip);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const trips = db.trips || [];
    const newTrip = { id: generateId(trips), ...req.body, createdAt: new Date().toISOString().split('T')[0] };
    trips.push(newTrip);
    db.trips = trips;
    writeDb(db);
    res.status(201).json(newTrip);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.put('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const trips = db.trips || [];
    const index = trips.findIndex(t => t.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Viaje no encontrado' });
    trips[index] = { ...trips[index], ...req.body, id: trips[index].id };
    db.trips = trips;
    writeDb(db);
    res.json(trips[index]);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const trips = db.trips || [];
    const index = trips.findIndex(t => t.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Viaje no encontrado' });
    const deleted = trips.splice(index, 1)[0];
    db.trips = trips;
    writeDb(db);
    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
