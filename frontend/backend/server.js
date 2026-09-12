import express from 'express';
import cors from 'cors';
import path from 'path';
import { fileURLToPath } from 'url';

import authRoutes from './routes/auth.js';
import usersRoutes from './routes/users.js';
import tripsRoutes from './routes/trips.js';
import carsRoutes from './routes/cars.js';
import bookingsRoutes from './routes/bookings.js';
import paymentsRoutes from './routes/payments.js';
import companiesRoutes from './routes/companies.js';
import partnersRoutes from './routes/partners.js';
import hotelsRoutes from './routes/hotels.js';
import reviewsRoutes from './routes/reviews.js';
import itinerariesRoutes from './routes/itineraries.js';
import budgetsRoutes from './routes/budgets.js';
import notificationsRoutes from './routes/notifications.js';

import { sendChatMessage, getChatSuggestions } from './routes/chat.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rutas públicas
app.use('/api/auth', authRoutes);
app.use('/api/trips', tripsRoutes);
app.use('/api/cars', carsRoutes);
app.use('/api/partners', partnersRoutes);
app.use('/api/hotels', hotelsRoutes);
app.use('/api/reviews', reviewsRoutes);
app.post('/api/chat/ask', sendChatMessage);
app.get('/api/chat/suggestions', getChatSuggestions);

// Rutas protegidas
app.use('/api/users', usersRoutes);
app.use('/api/bookings', bookingsRoutes);
app.use('/api/payments', paymentsRoutes);
app.use('/api/companies', companiesRoutes);
app.use('/api/itineraries', itinerariesRoutes);
app.use('/api/budgets', budgetsRoutes);
app.use('/api/notifications', notificationsRoutes);

// Servir archivos estáticos del frontend en producción
app.use(express.static(path.join(__dirname, '..', 'dist')));

app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', timestamp: new Date().toISOString() });
});

// Fallback SPA: redirigir a index.html cualquier ruta no API
app.use((req, res) => {
  res.sendFile(path.join(__dirname, '..', 'dist', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`🚀 TouristChain API corriendo en http://localhost:${PORT}`);
  console.log(`📊 Health check: http://localhost:${PORT}/api/health`);
  console.log(`🤖 Asistente IA: http://localhost:${PORT}/api/chat`);
  console.log(`💳 Pagos QR: http://localhost:${PORT}/api/payments/qr`);
});
