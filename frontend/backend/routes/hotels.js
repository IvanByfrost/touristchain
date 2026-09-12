import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken, requireAdmin } from '../middleware/auth.js';

const router = express.Router();

router.get('/', (req, res) => {
  try {
    const db = readDb();
    let hotels = db.hotels || [];
    const { city, stars, minPrice, maxPrice, search } = req.query;

    if (city) hotels = hotels.filter(h => h.city.toLowerCase().includes(city.toLowerCase()));
    if (stars) hotels = hotels.filter(h => h.stars === parseInt(stars));
    if (minPrice) hotels = hotels.filter(h => h.price >= parseFloat(minPrice));
    if (maxPrice) hotels = hotels.filter(h => h.price <= parseFloat(maxPrice));
    if (search) {
      const term = search.toLowerCase();
      hotels = hotels.filter(h => h.name.toLowerCase().includes(term) || h.city.toLowerCase().includes(term));
    }

    res.json(hotels);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.get('/:id', (req, res) => {
  try {
    const db = readDb();
    const hotel = (db.hotels || []).find(h => h.id === parseInt(req.params.id));
    if (!hotel) return res.status(404).json({ error: 'Hotel no encontrado' });
    res.json(hotel);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const hotels = db.hotels || [];
    const newHotel = { id: generateId(hotels), ...req.body };
    hotels.push(newHotel);
    db.hotels = hotels;
    writeDb(db);
    res.status(201).json(newHotel);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.put('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const hotels = db.hotels || [];
    const index = hotels.findIndex(h => h.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Hotel no encontrado' });
    hotels[index] = { ...hotels[index], ...req.body, id: hotels[index].id };
    db.hotels = hotels;
    writeDb(db);
    res.json(hotels[index]);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const hotels = db.hotels || [];
    const index = hotels.findIndex(h => h.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Hotel no encontrado' });
    const deleted = hotels.splice(index, 1)[0];
    db.hotels = hotels;
    writeDb(db);
    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
