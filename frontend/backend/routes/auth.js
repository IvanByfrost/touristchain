import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { generateToken, authenticateToken } from '../middleware/auth.js';

const router = express.Router();

// Anti fuerza bruta: máximo 8 intentos cada 10 minutos por email+IP
const loginAttempts = new Map();
const MAX_ATTEMPTS = 8;
const WINDOW_MS = 10 * 60 * 1000;

function loginRateLimit(req, res, next) {
  const key = `${req.ip}:${(req.body?.email || '').toLowerCase()}`;
  const now = Date.now();
  const entry = loginAttempts.get(key) || { count: 0, resetAt: now + WINDOW_MS };
  if (now > entry.resetAt) {
    entry.count = 0;
    entry.resetAt = now + WINDOW_MS;
  }
  entry.count += 1;
  loginAttempts.set(key, entry);
  if (entry.count > MAX_ATTEMPTS) {
    const waitMin = Math.ceil((entry.resetAt - now) / 60000);
    return res.status(429).json({ error: `Demasiados intentos. Intenta de nuevo en ${waitMin} min.` });
  }
  next();
}

function clearLoginAttempts(req) {
  loginAttempts.delete(`${req.ip}:${(req.body?.email || '').toLowerCase()}`);
}

router.post('/login', loginRateLimit, async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: 'Email y contraseña son requeridos' });
    }

    const db = readDb();
    const user = db.users.find(u => u.email === email);

    if (!user) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }

    const isValidPassword = await bcrypt.compare(password, user.password);

    if (!isValidPassword) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }

    if (user.status !== 'activo') {
      return res.status(403).json({ error: 'Usuario inactivo o pendiente' });
    }

    user.lastLogin = new Date().toISOString().split('T')[0];
    writeDb(db);
    clearLoginAttempts(req);

    const token = generateToken(user);

    res.json({
      token,
      user: sanitizeUser(user)
    });
  } catch (error) {
    console.error('Error en login:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/register', async (req, res) => {
  try {
    const { name, email, password, phone } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({ error: 'Nombre, email y contraseña son requeridos' });
    }

    if (password.length < 8) {
      return res.status(400).json({ error: 'La contraseña debe tener al menos 8 caracteres' });
    }

    const db = readDb();

    if (db.users.some(u => u.email === email)) {
      return res.status(409).json({ error: 'El email ya está registrado' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = {
      id: generateId(db.users),
      name,
      email,
      password: hashedPassword,
      role: 'user',
      status: 'activo',
      phone: phone || '',
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(email)}`,
      bio: '',
      location: '',
      loyaltyPoints: 100,
      preferences: { email: true, sms: false, push: true },
      createdAt: new Date().toISOString().split('T')[0],
      lastLogin: null
    };

    db.users.push(newUser);
    writeDb(db);

    const token = generateToken(newUser);

    res.status(201).json({
      token,
      user: sanitizeUser(newUser)
    });
  } catch (error) {
    console.error('Error en registro:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.get('/me', authenticateToken, async (req, res) => {
  try {
    const db = readDb();
    const user = db.users.find(u => u.id === req.user.id);

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    res.json(sanitizeUser(user));
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.put('/profile', authenticateToken, async (req, res) => {
  try {
    const { name, phone, bio, location, avatar, preferences } = req.body;
    const db = readDb();
    const user = db.users.find(u => u.id === req.user.id);

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    if (name !== undefined) user.name = name;
    if (phone !== undefined) user.phone = phone;
    if (bio !== undefined) user.bio = bio;
    if (location !== undefined) user.location = location;
    if (avatar !== undefined) user.avatar = avatar;
    if (preferences !== undefined) user.preferences = { ...user.preferences, ...preferences };

    user.updatedAt = new Date().toISOString();
    writeDb(db);

    res.json(sanitizeUser(user));
  } catch (error) {
    console.error('Error actualizando perfil:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.patch('/password', authenticateToken, async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;

    if (!currentPassword || !newPassword || newPassword.length < 8) {
      return res.status(400).json({ error: 'Contraseña actual y nueva válida son requeridas' });
    }

    const db = readDb();
    const user = db.users.find(u => u.id === req.user.id);

    if (!user) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const isValid = await bcrypt.compare(currentPassword, user.password);
    if (!isValid) {
      return res.status(401).json({ error: 'Contraseña actual incorrecta' });
    }

    user.password = await bcrypt.hash(newPassword, 10);
    user.updatedAt = new Date().toISOString();
    writeDb(db);

    res.json({ message: 'Contraseña actualizada exitosamente' });
  } catch (error) {
    console.error('Error cambiando contraseña:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/auth/forgot - solicitar código de recuperación (demo: se devuelve el código)
router.post('/forgot', (req, res) => {
  try {
    const { email } = req.body;
    if (!email) return res.status(400).json({ error: 'El correo es requerido' });

    const db = readDb();
    const user = db.users.find(u => u.email === String(email).toLowerCase().trim());
    // Respuesta genérica para no revelar qué correos existen
    if (!user) return res.json({ message: 'Si el correo existe, recibirás un código de recuperación.' });

    const code = String(Math.floor(100000 + Math.random() * 900000));
    db.resets = (db.resets || []).filter(r => r.email !== user.email);
    db.resets.push({ email: user.email, code, expires: Date.now() + 15 * 60 * 1000 });
    writeDb(db);

    // DEMO: en producción este código se enviaría por email/SMS
    res.json({ message: 'Si el correo existe, recibirás un código de recuperación.', resetCode: code });
  } catch (error) {
    console.error('Error en forgot:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/auth/reset - restablecer contraseña con el código
router.post('/reset', async (req, res) => {
  try {
    const { email, code, newPassword } = req.body;
    if (!email || !code || !newPassword) {
      return res.status(400).json({ error: 'Correo, código y nueva contraseña son requeridos' });
    }
    if (newPassword.length < 8) {
      return res.status(400).json({ error: 'La nueva contraseña debe tener al menos 8 caracteres' });
    }

    const db = readDb();
    const entry = (db.resets || []).find(r => r.email === String(email).toLowerCase().trim() && r.code === String(code));
    if (!entry || entry.expires < Date.now()) {
      return res.status(400).json({ error: 'Código inválido o vencido. Solicita uno nuevo.' });
    }
    const user = db.users.find(u => u.email === entry.email);
    if (!user) return res.status(404).json({ error: 'Usuario no encontrado' });

    user.password = await bcrypt.hash(newPassword, 10);
    user.updatedAt = new Date().toISOString();
    db.resets = (db.resets || []).filter(r => r.email !== entry.email);
    writeDb(db);

    res.json({ message: 'Contraseña restablecida. Ya puedes ingresar.' });
  } catch (error) {
    console.error('Error en reset:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

function sanitizeUser(user) {
  return {
    id: user.id,
    name: user.name,
    email: user.email,
    role: user.role,
    status: user.status,
    phone: user.phone,
    avatar: user.avatar,
    bio: user.bio,
    location: user.location,
    loyaltyPoints: user.loyaltyPoints,
    preferences: user.preferences,
    partnerProfile: user.partnerProfile || null,
    createdAt: user.createdAt,
    lastLogin: user.lastLogin
  };
}

export default router;
