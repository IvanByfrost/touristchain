import express from 'express';
import bcrypt from 'bcryptjs';
import { readDb, writeDb, generateId } from '../utils/db.js';
import { authenticateToken, requireAdmin, requireRole } from '../middleware/auth.js';

const router = express.Router();

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

// GET /api/partners - vitrina pública de socios
router.get('/', (req, res) => {
  try {
    const db = readDb();
    const { type, search } = req.query;
    let items = db.partners || [];
    if (type) items = items.filter(p => p.type === type);
    if (search) {
      const term = search.toLowerCase();
      items = items.filter(p => p.name.toLowerCase().includes(term));
    }
    res.json(items);
  } catch (error) {
    console.error('Error al obtener socios:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// GET /api/partners/me - perfil del socio autenticado
router.get('/me', authenticateToken, requireRole('socio', 'admin'), (req, res) => {
  try {
    const db = readDb();
    const user = db.users.find(u => u.id === req.user.id);
    if (!user) return res.status(404).json({ error: 'Usuario no encontrado' });
    const showcase = (db.partners || []).find(p => p.userId === user.id) || null;
    res.json({ user: sanitizeUser(user), showcase });
  } catch (error) {
    console.error('Error al obtener perfil de socio:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// PUT /api/partners/me - el socio actualiza sus datos básicos (NIT solo lo cambia el admin)
router.put('/me', authenticateToken, requireRole('socio'), (req, res) => {
  try {
    const { companyName, contactName, phone, address, city, type, description, website, logo, benefits } = req.body;
    const db = readDb();
    const user = db.users.find(u => u.id === req.user.id);
    if (!user) return res.status(404).json({ error: 'Usuario no encontrado' });

    user.partnerProfile = user.partnerProfile || {};
    if (companyName !== undefined) {
      user.partnerProfile.companyName = companyName;
      user.name = companyName;
    }
    if (contactName !== undefined) user.partnerProfile.contactName = contactName;
    if (phone !== undefined) { user.partnerProfile.phone = phone; user.phone = phone; }
    if (address !== undefined) user.partnerProfile.address = address;
    if (city !== undefined) { user.partnerProfile.city = city; user.location = city; }
    if (type !== undefined) user.partnerProfile.type = type;
    user.updatedAt = new Date().toISOString();

    let showcase = (db.partners || []).find(p => p.userId === user.id);
    if (showcase) {
      if (companyName !== undefined) showcase.name = companyName;
      if (type !== undefined) showcase.type = type;
      if (description !== undefined) showcase.description = description;
      if (website !== undefined) showcase.website = website;
      if (logo !== undefined) showcase.logo = logo;
      if (benefits !== undefined) showcase.benefits = Array.isArray(benefits) ? benefits : String(benefits).split('\n').map(b => b.trim()).filter(Boolean);
    }
    writeDb(db);
    res.json({ user: sanitizeUser(user), showcase: showcase || null });
  } catch (error) {
    console.error('Error al actualizar socio:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/partners/accounts - el admin crea un socio (usuario + vitrina) con NIT y datos básicos
router.post('/accounts', authenticateToken, requireAdmin, async (req, res) => {
  try {
    const { companyName, nit, contactName, email, password, phone, address, city, type, description, website } = req.body;

    if (!companyName || !nit || !contactName || !email || !password) {
      return res.status(400).json({ error: 'Empresa, NIT, contacto, email y contraseña son requeridos' });
    }
    if (password.length < 8) {
      return res.status(400).json({ error: 'La contraseña debe tener al menos 8 caracteres' });
    }

    const db = readDb();
    if (db.users.some(u => u.email === email)) {
      return res.status(409).json({ error: 'El email ya está registrado' });
    }
    if ((db.users || []).some(u => u.partnerProfile && u.partnerProfile.nit === nit)) {
      return res.status(409).json({ error: 'El NIT ya está registrado' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const userId = generateId(db.users);
    const newUser = {
      id: userId,
      name: companyName,
      email,
      password: hashedPassword,
      role: 'socio',
      status: 'activo',
      phone: phone || '',
      avatar: `https://api.dicebear.com/7.x/shapes/svg?seed=${encodeURIComponent(nit)}`,
      bio: description || '',
      location: city || '',
      loyaltyPoints: 0,
      preferences: { email: true, sms: false, push: true },
      partnerProfile: {
        companyName,
        nit,
        contactName,
        phone: phone || '',
        address: address || '',
        city: city || '',
        type: type || 'hotel'
      },
      createdAt: new Date().toISOString().split('T')[0],
      lastLogin: null
    };
    db.users.push(newUser);

    const partners = db.partners || [];
    const showcase = {
      id: generateId(partners),
      userId,
      name: companyName,
      type: type || 'hotel',
      description: description || `Socio TouristChain - ${companyName}`,
      logo: `https://via.placeholder.com/150/1a2a6c/ffffff?text=${encodeURIComponent(companyName.substring(0, 12))}`,
      website: website || '#',
      benefits: [],
      status: 'activo'
    };
    partners.push(showcase);
    db.partners = partners;
    writeDb(db);

    res.status(201).json({ user: sanitizeUser(newUser), showcase });
  } catch (error) {
    console.error('Error al crear socio:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// PUT /api/partners/accounts/:id - el admin edita NIT y datos del socio
router.put('/accounts/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const { companyName, nit, contactName, phone, address, city, type, status } = req.body;
    const db = readDb();
    const user = db.users.find(u => u.id === parseInt(req.params.id) && u.role === 'socio');
    if (!user) return res.status(404).json({ error: 'Socio no encontrado' });

    if (nit && nit !== user.partnerProfile?.nit) {
      if ((db.users || []).some(u => u.id !== user.id && u.partnerProfile && u.partnerProfile.nit === nit)) {
        return res.status(409).json({ error: 'El NIT ya está registrado' });
      }
      user.partnerProfile.nit = nit;
    }
    user.partnerProfile = user.partnerProfile || {};
    if (companyName !== undefined) { user.partnerProfile.companyName = companyName; user.name = companyName; }
    if (contactName !== undefined) user.partnerProfile.contactName = contactName;
    if (phone !== undefined) { user.partnerProfile.phone = phone; user.phone = phone; }
    if (address !== undefined) user.partnerProfile.address = address;
    if (city !== undefined) { user.partnerProfile.city = city; user.location = city; }
    if (type !== undefined) user.partnerProfile.type = type;
    if (status !== undefined) user.status = status;
    user.updatedAt = new Date().toISOString();

    const showcase = (db.partners || []).find(p => p.userId === user.id);
    if (showcase) {
      if (companyName !== undefined) showcase.name = companyName;
      if (type !== undefined) showcase.type = type;
      if (status !== undefined) showcase.status = status;
    }
    writeDb(db);
    res.json({ user: sanitizeUser(user), showcase: showcase || null });
  } catch (error) {
    console.error('Error al editar socio:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// POST /api/partners - crear vitrina (admin)
router.post('/', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const partners = db.partners || [];
    const newItem = { id: generateId(partners), status: 'activo', ...req.body, createdAt: new Date().toISOString().split('T')[0] };
    partners.push(newItem);
    db.partners = partners;
    writeDb(db);
    res.status(201).json(newItem);
  } catch (error) {
    console.error('Error al crear vitrina:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// PUT /api/partners/:id - editar vitrina (admin)
router.put('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const partners = db.partners || [];
    const index = partners.findIndex(p => p.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Socio no encontrado' });
    partners[index] = { ...partners[index], ...req.body, id: partners[index].id };
    db.partners = partners;
    writeDb(db);
    res.json(partners[index]);
  } catch (error) {
    console.error('Error al editar vitrina:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// DELETE /api/partners/:id - eliminar vitrina (admin)
router.delete('/:id', authenticateToken, requireAdmin, (req, res) => {
  try {
    const db = readDb();
    const partners = db.partners || [];
    const index = partners.findIndex(p => p.id === parseInt(req.params.id));
    if (index === -1) return res.status(404).json({ error: 'Socio no encontrado' });
    const deleted = partners.splice(index, 1)[0];
    db.partners = partners;
    writeDb(db);
    res.json(deleted);
  } catch (error) {
    console.error('Error al eliminar vitrina:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

export default router;
