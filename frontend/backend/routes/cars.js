import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken, requireAdmin } from '../middleware/auth.js';

const router = express.Router();

router.get('/', (req, res) => {
  try {
    const db = readDb();
    res.json(db.cars || []);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.get('/:id', (req, res) => {
  try {
    const db = readDb();
    const car = (db.cars || []).find(c => c.id === parseInt(req.params.id));
    if (!car) return res.status(404).json({ error: 'Vehículo no encontrado' });
    res.json(car);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const cars = db.cars || [];
    const newCar = { id: generateId(cars), ...req.body };
    cars.push(newCar);
    db.cars = cars;
    writeDb(db);
    res.status(201).json(newCar);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.put('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const cars = db.cars || [];
    const index = cars.findIndex(c => c.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Vehículo no encontrado' });
    cars[index] = { ...cars[index], ...req.body, id: cars[index].id };
    db.cars = cars;
    writeDb(db);
    res.json(cars[index]);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const cars = db.cars || [];
    const index = cars.findIndex(c => c.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Vehículo no encontrado' });
    const deleted = cars.splice(index, 1)[0];
    db.cars = cars;
    writeDb(db);
    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
