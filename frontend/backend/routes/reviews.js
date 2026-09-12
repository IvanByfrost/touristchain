import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

router.get('/', (req, res) => {
  try {
    const db = readDb();
    let reviews = db.reviews || [];
    const { itemType, itemId } = req.query;

    if (itemType) reviews = reviews.filter(r => r.itemType === itemType);
    if (itemId) reviews = reviews.filter(r => r.itemId === parseInt(itemId));

    res.json(reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
  } catch (error) {
    console.error('Error al obtener reviews:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.get('/stats', (req, res) => {
  try {
    const db = readDb();
    const reviews = db.reviews || [];
    const { itemType, itemId } = req.query;

    let filtered = reviews;
    if (itemType) filtered = filtered.filter(r => r.itemType === itemType);
    if (itemId) filtered = filtered.filter(r => r.itemId === parseInt(itemId));

    const avg = filtered.length ? filtered.reduce((sum, r) => sum + r.rating, 0) / filtered.length : 0;
    res.json({ average: parseFloat(avg.toFixed(1)), count: filtered.length });
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/', authenticateToken, (req, res) => {
  try {
    const { itemType, itemId, itemName, rating, comment } = req.body;

    if (!itemType || !itemId || !rating || rating < 1 || rating > 5) {
      return res.status(400).json({ error: 'Tipo, ID y rating (1-5) son requeridos' });
    }

    const db = readDb();
    const reviews = db.reviews || [];

    const existing = reviews.find(r => r.userId === req.user.id && r.itemType === itemType && r.itemId === parseInt(itemId));
    if (existing) {
      return res.status(409).json({ error: 'Ya has reseñado este item' });
    }

    const newReview = {
      id: generateId(reviews),
      userId: req.user.id,
      userName: req.user.name,
      avatar: req.user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${req.user.id}`,
      itemType,
      itemId: parseInt(itemId),
      itemName: itemName || '',
      rating: parseInt(rating),
      comment: comment || '',
      createdAt: new Date().toISOString()
    };

    reviews.push(newReview);
    db.reviews = reviews;
    writeDb(db);

    res.status(201).json(newReview);
  } catch (error) {
    console.error('Error al crear review:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const reviews = db.reviews || [];
    const index = reviews.findIndex(r => r.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Reseña no encontrada' });
    if (req.user.role !== 'admin' && reviews[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    const deleted = reviews.splice(index, 1)[0];
    db.reviews = reviews;
    writeDb(db);

    res.json(deleted);
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
