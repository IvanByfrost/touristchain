import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const DB_PATH = path.join(__dirname, '..', 'data', 'db.json');

const defaultData = {
  users: [
    {
      id: 1,
      name: 'Administrador Principal',
      email: 'admin@touristchain.com',
      password: '$2b$10$CG.PcrrifGHvxHRgsT3LPeTG.dOySz7wTwbQ/kaRmojIZflftSsuq',
      role: 'admin',
      status: 'activo',
      phone: '+34 123 456 789',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin',
      bio: 'Administrador de TouristChain',
      location: 'Barcelona, España',
      loyaltyPoints: 0,
      preferences: { email: true, sms: false, push: true },
      createdAt: '2024-01-15',
      lastLogin: null
    },
    {
      id: 2,
      name: 'Juan Pérez',
      email: 'juan@example.com',
      password: '$2b$10$CG.PcrrifGHvxHRgsT3LPeTG.dOySz7wTwbQ/kaRmojIZflftSsuq',
      role: 'user',
      status: 'activo',
      phone: '+34 123 456 789',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Juan',
      bio: 'Amante de los viajes y la aventura',
      location: 'Madrid, España',
      loyaltyPoints: 250,
      preferences: { email: true, sms: false, push: true },
      createdAt: '2024-01-15',
      lastLogin: null
    }
  ],
  trips: [
    {
      id: 1,
      destination: 'París, Francia',
      description: 'Tour por la ciudad del amor con visita a la Torre Eiffel, Louvre y paseo en barco por el Sena',
      startDate: '2024-12-15',
      endDate: '2024-12-22',
      price: 1200,
      capacity: 20,
      available: 15,
      status: 'disponible',
      image: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34',
      rating: 4.8,
      reviews: 12,
      tags: ['romántico', 'cultura', 'europa'],
      coordinates: { lat: 48.8566, lng: 2.3522 },
      createdAt: '2024-10-01'
    },
    {
      id: 2,
      destination: 'Roma, Italia',
      description: 'Historia y cultura en la Ciudad Eterna con visitas al Coliseo, Vaticano y Fontana di Trevi',
      startDate: '2024-12-20',
      endDate: '2024-12-27',
      price: 1100,
      capacity: 15,
      available: 8,
      status: 'disponible',
      image: 'https://images.unsplash.com/photo-1552832230-c0197dd311b5',
      rating: 4.7,
      reviews: 9,
      tags: ['historia', 'cultura', 'gastronomía'],
      coordinates: { lat: 41.9028, lng: 12.4964 },
      createdAt: '2024-10-05'
    },
    {
      id: 3,
      destination: 'Tokio, Japón',
      description: 'Aventura tecnológica y cultural en la capital japonesa',
      startDate: '2025-01-10',
      endDate: '2025-01-20',
      price: 2200,
      capacity: 12,
      available: 4,
      status: 'reservado',
      image: 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf',
      rating: 4.9,
      reviews: 15,
      tags: ['tecnología', 'cultura', 'asia'],
      coordinates: { lat: 35.6762, lng: 139.6503 },
      createdAt: '2024-10-10'
    },
    {
      id: 4,
      destination: 'Cancún, México',
      description: '5 días en resorts todo incluido con playas de arena blanca y mar caribe',
      startDate: '2025-02-15',
      endDate: '2025-02-20',
      price: 1200,
      capacity: 25,
      available: 18,
      status: 'disponible',
      image: 'https://images.unsplash.com/photo-1511895426328-dc8714191300',
      rating: 4.6,
      reviews: 22,
      tags: ['playa', 'relax', 'caribe'],
      coordinates: { lat: 21.1619, lng: -86.8515 },
      createdAt: '2024-10-15'
    },
    {
      id: 5,
      destination: 'Nueva York, USA',
      description: 'Descubre la ciudad que nunca duerme - Broadway, museos y más',
      startDate: '2025-03-01',
      endDate: '2025-03-05',
      price: 1500,
      capacity: 18,
      available: 10,
      status: 'disponible',
      image: 'https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9',
      rating: 4.7,
      reviews: 18,
      tags: ['ciudad', 'compras', 'norteamérica'],
      coordinates: { lat: 40.7128, lng: -74.006 },
      createdAt: '2024-10-20'
    },
    {
      id: 6,
      destination: 'Bali, Indonesia',
      description: 'Relax y cultura en las paradisíacas islas de Indonesia',
      startDate: '2025-03-10',
      endDate: '2025-03-17',
      price: 1400,
      capacity: 14,
      available: 7,
      status: 'disponible',
      image: 'https://images.unsplash.com/photo-1537996194471-e657df975ab4',
      rating: 4.8,
      reviews: 11,
      tags: ['playa', 'naturaleza', 'asia'],
      coordinates: { lat: -8.4095, lng: 115.1889 },
      createdAt: '2024-10-25'
    }
  ],
  hotels: [
    {
      id: 1,
      name: 'Marriott Champs-Élysées',
      city: 'París, Francia',
      stars: 5,
      price: 280,
      image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945',
      description: 'Hotel de lujo con vista a la Torre Eiffel y servicio premium.',
      amenities: ['WiFi', 'Spa', 'Piscina', 'Gimnasio', 'Restaurante'],
      coordinates: { lat: 48.8738, lng: 2.295 }
    },
    {
      id: 2,
      name: 'Hotel Artis',
      city: 'Roma, Italia',
      stars: 4,
      price: 150,
      image: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa',
      description: 'Hotel boutique cerca del Coliseo con encanto italiano.',
      amenities: ['WiFi', 'Desayuno', 'Terraza', 'Aire acondicionado'],
      coordinates: { lat: 41.89, lng: 12.49 }
    },
    {
      id: 3,
      name: 'Park Hyatt Tokyo',
      city: 'Tokio, Japón',
      stars: 5,
      price: 450,
      image: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb',
      description: 'Hotel icónico con vistas panorámicas de Tokio y monte Fuji.',
      amenities: ['WiFi', 'Spa', 'Piscina', 'Gimnasio', 'Bar'],
      coordinates: { lat: 35.685, lng: 139.69 }
    },
    {
      id: 4,
      name: 'The Westin Cancún',
      city: 'Cancún, México',
      stars: 4,
      price: 220,
      image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d',
      description: 'Resort frente al mar con playa privada y actividades acuáticas.',
      amenities: ['WiFi', 'Playa', 'Piscina', 'Restaurante', 'Bar'],
      coordinates: { lat: 21.14, lng: -86.77 }
    },
    {
      id: 5,
      name: 'The Plaza New York',
      city: 'Nueva York, USA',
      stars: 5,
      price: 520,
      image: 'https://images.unsplash.com/photo-1564501049412-61c2a3083791',
      description: 'Hotel histórico en Fifth Avenue, símbolo de lujo neoyorquino.',
      amenities: ['WiFi', 'Spa', 'Gimnasio', 'Restaurante', 'Concierge'],
      coordinates: { lat: 40.7647, lng: -73.9748 }
    },
    {
      id: 6,
      name: 'Ayana Resort Bali',
      city: 'Bali, Indonesia',
      stars: 5,
      price: 350,
      image: 'https://images.unsplash.com/photo-1537996194471-e657df975ab4',
      description: 'Resort de lujo con villas privadas y vistas al océano Índico.',
      amenities: ['WiFi', 'Spa', 'Piscina infinita', 'Playa', 'Restaurante'],
      coordinates: { lat: -8.79, lng: 115.21 }
    },
    {
      id: 7,
      name: 'JW Marriott Bogotá',
      city: 'Bogotá, Colombia',
      stars: 5,
      price: 180,
      image: 'https://images.unsplash.com/photo-1618773928121-c32242e63f39',
      description: 'Hotel moderno en el corazón financiero de Bogotá.',
      amenities: ['WiFi', 'Spa', 'Gimnasio', 'Restaurante', 'Bar'],
      coordinates: { lat: 4.68, lng: -74.05 }
    },
    {
      id: 8,
      name: 'Hotel Barcelona Center',
      city: 'Barcelona, España',
      stars: 4,
      price: 160,
      image: 'https://images.unsplash.com/photo-1563911302283-d2bc129e7c1f',
      description: 'Elegante hotel cerca de Paseo de Gracia y La Sagrada Familia.',
      amenities: ['WiFi', 'Terraza', 'Desayuno', 'Gimnasio'],
      coordinates: { lat: 41.39, lng: 2.16 }
    }
  ],
  cars: [
    {
      id: 1,
      name: 'Toyota Yaris',
      type: 'Compacto',
      category: 'basico',
      price: 35,
      image: 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2',
      features: ['5 personas', 'Automático', 'Aire acondicionado', 'Bluetooth'],
      rating: 4.5,
      reviews: 8
    },
    {
      id: 2,
      name: 'Honda CR-V',
      type: 'SUV',
      category: 'medio',
      price: 65,
      image: 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341',
      features: ['7 personas', 'Automático', 'GPS', 'WiFi', 'Cámara trasera'],
      rating: 4.7,
      reviews: 12
    },
    {
      id: 3,
      name: 'BMW X5',
      type: 'Premium',
      category: 'black',
      price: 120,
      image: 'https://images.unsplash.com/photo-1555212697-194d092e3b8f',
      features: ['5 personas', 'Automático', 'Pantalla táctil', 'Asientos de cuero', 'Sistema premium'],
      rating: 4.9,
      reviews: 6
    }
  ],
  bookings: [],
  payments: [
    {
      id: 1,
      userId: 2,
      userName: 'Juan Pérez',
      amount: 1200,
      method: 'tarjeta',
      date: '2024-12-01',
      status: 'completado',
      reference: 'PAY-001',
      description: 'Reserva París 2024'
    }
  ],
  companies: [
    {
      id: 1,
      ruc: '12345678901',
      name: 'Viajes del Sol S.A.',
      contact: 'Roberto Gómez',
      email: 'info@viajessol.com',
      phone: '+34 900 123 456',
      address: 'Av. del Turismo 123, Barcelona',
      status: 'activa',
      workers: 5,
      createdAt: '2024-01-10'
    },
    {
      id: 2,
      ruc: '98765432109',
      name: 'Aventuras Extremas S.L.',
      contact: 'Laura Méndez',
      email: 'contacto@aventuras.com',
      phone: '+34 900 654 321',
      address: 'Calle Aventura 45, Madrid',
      status: 'activa',
      workers: 8,
      createdAt: '2024-02-15'
    }
  ],
  partners: [
    {
      id: 1,
      name: 'Hoteles Marriott',
      type: 'hotel',
      description: 'Cadena hotelera internacional con hospedaje premium',
      logo: 'https://via.placeholder.com/150/1a2a6c/ffffff?text=Marriott',
      website: 'https://marriott.com',
      benefits: ['Descuento 15%', 'Desayuno incluido', 'Upgrade de habitación'],
      status: 'activo'
    },
    {
      id: 2,
      name: 'Avis Rent A Car',
      type: 'renta',
      description: 'Servicio de renta de vehículos global',
      logo: 'https://via.placeholder.com/150/ff3860/ffffff?text=Avis',
      website: 'https://avis.com',
      benefits: ['KM ilimitados', 'Seguro incluido', '10% de descuento'],
      status: 'activo'
    },
    {
      id: 3,
      name: 'Air Europa',
      type: 'aerolinea',
      description: 'Aerolínea europea con vuelos internacionales',
      logo: 'https://via.placeholder.com/150/3298dc/ffffff?text=Air+Europa',
      website: 'https://aireuropa.com',
      benefits: ['Equipaje extra', 'Check-in prioritario', 'Millas duplicadas'],
      status: 'activo'
    }
  ],
  reviews: [
    {
      id: 1,
      userId: 2,
      userName: 'Juan Pérez',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Juan',
      itemType: 'viaje',
      itemId: 1,
      itemName: 'París, Francia',
      rating: 5,
      comment: 'Experiencia inolvidable, todo muy bien organizado.',
      createdAt: '2024-12-10'
    },
    {
      id: 2,
      userId: 2,
      userName: 'Juan Pérez',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Juan',
      itemType: 'car',
      itemId: 2,
      itemName: 'Honda CR-V',
      rating: 4,
      comment: 'Excelente vehículo, muy cómodo para el viaje.',
      createdAt: '2024-12-05'
    }
  ],
  itineraries: [],
  budgets: [],
  notifications: [],
  activity: []
};

function ensureDb() {
  if (!fs.existsSync(DB_PATH)) {
    fs.mkdirSync(path.dirname(DB_PATH), { recursive: true });
    fs.writeFileSync(DB_PATH, JSON.stringify(defaultData, null, 2));
  }
}

export function readDb() {
  ensureDb();
  return JSON.parse(fs.readFileSync(DB_PATH, 'utf-8'));
}

export function writeDb(data) {
  ensureDb();
  fs.writeFileSync(DB_PATH, JSON.stringify(data, null, 2));
}

export function getCollection(name) {
  const db = readDb();
  return db[name] || [];
}

export function setCollection(name, data) {
  const db = readDb();
  db[name] = data;
  writeDb(db);
}

export function generateId(collection) {
  if (collection.length === 0) return 1;
  return Math.max(...collection.map(item => item.id)) + 1;
}
