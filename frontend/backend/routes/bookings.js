import express from 'express';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

// GET /api/bookings - listar reservas del usuario (o todas para admin)
router.get('/', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    let bookings = db.bookings || [];
    if (req.user.role !== 'admin') {
      bookings = bookings.filter(b => b.userId === req.user.id);
    }
    res.json(bookings);
  } catch (error) {
    console.error('Error al obtener bookings:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// GET /api/bookings/:id - detalle de reserva
router.get('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const booking = (db.bookings || []).find(b => b.id === parseInt(req.params.id));
    if (!booking) return res.status(404).json({ error: 'Reserva no encontrada' });
    if (req.user.role !== 'admin' && booking.userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }
    res.json(booking);
  } catch (error) {
    console.error('Error al obtener booking:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/bookings - crear una reserva real
router.post('/', authenticateToken, (req, res) => {
  try {
    const { tripId, itemName, itemType, price, startDate, endDate, quantity = 1, notes } = req.body;

    if (!itemName || !itemType || !price) {
      return res.status(400).json({ error: 'Nombre, tipo y precio son requeridos' });
    }

    const db = readDb();
    const bookings = db.bookings || [];

    // Si es un viaje con tripId, verificar disponibilidad
    if (tripId && itemType === 'viaje') {
      const trip = (db.trips || []).find(t => t.id === parseInt(tripId));
      if (!trip) return res.status(404).json({ error: 'Viaje no encontrado' });
      if (trip.available < quantity) {
        return res.status(400).json({ error: `Solo quedan ${trip.available} lugares disponibles` });
      }
      trip.available -= quantity;
      trip.status = trip.available === 0 ? 'reservado' : trip.status;
    }

    const newBooking = {
      id: generateId(bookings),
      userId: req.user.id,
      userName: req.user.name || req.user.email,
      tripId: tripId || null,
      itemName,
      itemType,
      price: parseFloat(price),
      quantity: parseInt(quantity),
      total: parseFloat(price) * parseInt(quantity),
      startDate: startDate || null,
      endDate: endDate || null,
      status: 'pendiente',
      notes: notes || '',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    bookings.push(newBooking);
    db.bookings = bookings;
    writeDb(db);

    res.status(201).json(newBooking);
  } catch (error) {
    console.error('Error al crear booking:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// PUT /api/bookings/:id - actualizar reserva
router.put('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const bookings = db.bookings || [];
    const index = bookings.findIndex(b => b.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Reserva no encontrada' });
    if (req.user.role !== 'admin' && bookings[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    bookings[index] = { ...bookings[index], ...req.body, id: bookings[index].id, updatedAt: new Date().toISOString() };
    db.bookings = bookings;
    writeDb(db);

    res.json(bookings[index]);
  } catch (error) {
    console.error('Error al actualizar booking:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// DELETE /api/bookings/:id - cancelar/eliminar reserva (libera cupo si es viaje)
router.delete('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const bookings = db.bookings || [];
    const index = bookings.findIndex(b => b.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Reserva no encontrada' });
    if (req.user.role !== 'admin' && bookings[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    const booking = bookings[index];

    // Liberar cupo si es viaje con tripId
    if (booking.tripId && booking.itemType === 'viaje') {
      const trip = (db.trips || []).find(t => t.id === parseInt(booking.tripId));
      if (trip) {
        trip.available += booking.quantity;
        if (trip.status === 'reservado' && trip.available > 0) {
          trip.status = 'disponible';
        }
      }
    }

    const deleted = bookings.splice(index, 1)[0];
    db.bookings = bookings;
    writeDb(db);

    res.json(deleted);
  } catch (error) {
    console.error('Error al eliminar booking:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
