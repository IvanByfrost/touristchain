import express from 'express';
import QRCode from 'qrcode';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

// Helper: obtener pagos del usuario (o todos si es admin)
function getPayments(req) {
  const db = readDb();
  let payments = db.payments || [];
  if (req.user.role !== 'admin') {
    payments = payments.filter(p => p.userId === req.user.id);
  }
  return payments;
}

// GET /api/payments - listar pagos
router.get('/', authenticateToken, (req, res) => {
  try {
    res.json(getPayments(req));
  } catch (error) {
    console.error('Error al obtener pagos:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// GET /api/payments/:id - detalle de un pago
router.get('/:id', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const payment = (db.payments || []).find(p => p.id === parseInt(req.params.id));
    if (!payment) return res.status(404).json({ error: 'Pago no encontrado' });
    if (req.user.role !== 'admin' && payment.userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }
    res.json(payment);
  } catch (error) {
    console.error('Error al obtener pago:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/payments - crear un pago (tarjeta, nequi, pse - simulados, aprobación inmediata)
router.post('/', authenticateToken, (req, res) => {
  try {
    const { amount, method, description, reservationIds } = req.body;
    const allowed = ['tarjeta', 'nequi', 'qr', 'pse'];
    if (!amount || !method) {
      return res.status(400).json({ error: 'Monto y método son requeridos' });
    }
    if (!allowed.includes(method)) {
      return res.status(400).json({ error: 'Método de pago no soportado' });
    }

    const db = readDb();
    const payments = db.payments || [];
    const newPayment = {
      id: generateId(payments),
      userId: req.user.id,
      userName: req.user.name || req.user.email,
      amount: parseFloat(amount),
      method,
      status: method === 'qr' ? 'pendiente' : 'completado',
      date: new Date().toISOString().split('T')[0],
      reference: `TC-${Date.now().toString(36).toUpperCase()}`,
      description: description || 'Reserva TouristChain',
      reservationIds: reservationIds || []
    };

    payments.push(newPayment);
    db.payments = payments;
    writeDb(db);

    res.status(201).json(newPayment);
  } catch (error) {
    console.error('Error al crear pago:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/payments/qr - generar un pago con QR real
router.post('/qr', authenticateToken, async (req, res) => {
  try {
    const { amount, description, reservationIds } = req.body;
    if (!amount) {
      return res.status(400).json({ error: 'El monto es requerido' });
    }

    const db = readDb();
    const payments = db.payments || [];
    const paymentId = generateId(payments);
    const reference = `TC-${Date.now().toString(36).toUpperCase()}`;

    const newPayment = {
      id: paymentId,
      userId: req.user.id,
      userName: req.user.name || req.user.email,
      amount: parseFloat(amount),
      method: 'qr',
      status: 'pendiente',
      date: new Date().toISOString().split('T')[0],
      reference,
      description: description || 'Pago con QR - TouristChain',
      reservationIds: reservationIds || [],
      qrToken: `qr-${Date.now()}-${Math.random().toString(36).substring(2)}`
    };

    payments.push(newPayment);
    db.payments = payments;
    writeDb(db);

    // Generar el contenido del QR: URL de confirmación o datos del pago
    // Se usa el host real de la petición para que el QR sea escaneable en red local
    const baseUrl = `${req.protocol}://${req.get('host')}`;
    const qrPayload = JSON.stringify({
      touristchain: true,
      paymentId,
      reference,
      amount: newPayment.amount,
      merchant: 'TouristChain',
      url: `${baseUrl}/api/payments/qr/${newPayment.qrToken}/confirm`
    });

    const qrImage = await QRCode.toDataURL(qrPayload, {
      width: 300,
      margin: 2,
      color: {
        dark: '#1a2a6c',
        light: '#ffffff'
      }
    });

    res.status(201).json({
      payment: newPayment,
      qrImage,
      qrPayload
    });
  } catch (error) {
    console.error('Error al generar QR:', error);
    res.status(500).json({ error: 'Error al generar el código QR' });
  }
});

// GET /api/payments/qr/:token/confirm - confirmar pago escaneando QR (simula pasarela)
router.get('/qr/:token/confirm', async (req, res) => {
  try {
    const db = readDb();
    const payments = db.payments || [];
    const index = payments.findIndex(p => p.qrToken === req.params.token);

    if (index === -1) {
      return res.status(404).send(`
        <!DOCTYPE html>
        <html lang="es">
        <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pago no encontrado</title>
        <style>body{font-family:system-ui,sans-serif;text-align:center;padding:3rem;background:#f8f9fa;}</style>
        </head>
        <body>
          <h1 style="color:#dc3545;">Pago no encontrado</h1>
          <p>El código QR escaneado no corresponde a ninguna transacción válida.</p>
        </body>
        </html>
      `);
    }

    if (payments[index].status === 'completado') {
      return res.send(buildQrResponsePage('Pago ya completado', 'Este pago ya fue procesado anteriormente.', 'success'));
    }

    payments[index].status = 'completado';
    payments[index].paidAt = new Date().toISOString();
    db.payments = payments;
    writeDb(db);

    // Actualizar bookings asociados a pagados
    updateBookingsAsPaid(db, payments[index]);

    res.send(buildQrResponsePage('¡Pago Exitoso!', `Se confirmó el pago #${payments[index].reference} por $${payments[index].amount.toLocaleString()}.`, 'success'));
  } catch (error) {
    console.error('Error al confirmar QR:', error);
    res.status(500).send(buildQrResponsePage('Error', 'No se pudo procesar el pago. Intente nuevamente.', 'error'));
  }
});

// POST /api/payments/:id/confirm - confirmar pago manualmente
router.post('/:id/confirm', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const payments = db.payments || [];
    const index = payments.findIndex(p => p.id === parseInt(req.params.id));

    if (index === -1) return res.status(404).json({ error: 'Pago no encontrado' });
    if (req.user.role !== 'admin' && payments[index].userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }

    payments[index].status = 'completado';
    payments[index].paidAt = new Date().toISOString();
    db.payments = payments;
    writeDb(db);

    updateBookingsAsPaid(db, payments[index]);

    res.json(payments[index]);
  } catch (error) {
    console.error('Error al confirmar pago:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// GET /api/payments/:id/status - estado de un pago
router.get('/:id/status', authenticateToken, (req, res) => {
  try {
    const db = readDb();
    const payment = (db.payments || []).find(p => p.id === parseInt(req.params.id));
    if (!payment) return res.status(404).json({ error: 'Pago no encontrado' });
    if (req.user.role !== 'admin' && payment.userId !== req.user.id) {
      return res.status(403).json({ error: 'Acceso denegado' });
    }
    res.json({ id: payment.id, status: payment.status, reference: payment.reference });
  } catch (error) {
    console.error('Error al obtener estado:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// DELETE /api/payments/:id - eliminar pago (admin)
router.delete('/:id', authenticateToken, (req, res) => {
  try {
    if (req.user.role !== 'admin') {
      return res.status(403).json({ error: 'Solo administradores pueden eliminar pagos' });
    }
    const db = readDb();
    const payments = db.payments || [];
    const index = payments.findIndex(p => p.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Pago no encontrado' });
    const deleted = payments.splice(index, 1)[0];
    db.payments = payments;
    writeDb(db);
    res.json(deleted);
  } catch (error) {
    console.error('Error al eliminar pago:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

function updateBookingsAsPaid(db, payment) {
  if (!payment.reservationIds || payment.reservationIds.length === 0) return;
  const bookings = db.bookings || [];
  bookings.forEach(b => {
    if (payment.reservationIds.includes(b.id)) {
      b.status = 'pagado';
      b.paymentId = payment.id;
      b.paidAt = payment.paidAt;
    }
  });
  db.bookings = bookings;
  writeDb(db);
}

function buildQrResponsePage(title, message, type) {
  const color = type === 'success' ? '#28a745' : '#dc3545';
  return `
    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>${title} - TouristChain</title>
      <style>
        *{box-sizing:border-box;}
        body{font-family:system-ui,-apple-system,sans-serif;margin:0;padding:0;background:linear-gradient(135deg,#1a2a6c,#2d4ba8);min-height:100vh;display:flex;align-items:center;justify-content:center;color:#333;}
        .card{background:white;border-radius:1.5rem;padding:2.5rem;max-width:420px;width:90%;text-align:center;box-shadow:0 20px 60px rgba(0,0,0,.3);}
        .icon{font-size:4rem;margin-bottom:1rem;}
        h1{margin:0 0 .5rem;color:${color};font-size:1.75rem;}
        p{font-size:1.1rem;line-height:1.5;color:#555;margin-bottom:1.5rem;}
        .btn{display:inline-block;background:linear-gradient(to right,#1a2a6c,#4a6fff);color:white;text-decoration:none;padding:.875rem 1.75rem;border-radius:2rem;font-weight:700;transition:transform .2s;}
        .btn:hover{transform:translateY(-2px);}
      </style>
    </head>
    <body>
      <div class="card">
        <div class="icon">${type === 'success' ? '✅' : '❌'}</div>
        <h1>${title}</h1>
        <p>${message}</p>
        <a href="/" class="btn">Volver a TouristChain</a>
      </div>
    </body>
    </html>
  `;
}

export default router;
