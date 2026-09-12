import express from 'express';
import { readDb } from '../utils/db.js';

const router = express.Router();

// Base de conocimiento del asistente virtual
const knowledgeBase = [
  {
    keywords: ['hola', 'buenas', 'hey', 'saludos', 'buenos dias', 'buenas tardes', 'buenas noches'],
    response: '¡Hola! Soy el asistente virtual de TouristChain. Puedo ayudarte con destinos, precios, reservas, pagos y mucho más. ¿En qué puedo ayudarte hoy?'
  },
  {
    keywords: ['precio', 'costo', 'cuanto cuesta', 'cuánto cuesta', 'tarifa', 'valor'],
    response: 'Los precios varían según el destino y paquete. Por ejemplo: Cancún desde $799, Nueva York desde $1,500, Tokio desde $1,800 y Roma desde $1,100. También tenemos renta de carros desde $35/día. ¿Te gustaría ver opciones para un destino específico?'
  },
  {
    keywords: ['destino', 'viajes', 'lugares', 'paises', 'ciudades', 'disponible'],
    response: 'Tenemos destinos increíbles: Cancún, Andes, Nueva York, Roma, Tokio, Bali y más. También puedes crear viajes personalizados. ¿Tienes algún destino en mente o prefieres que te recomiende según tu presupuesto?'
  },
  {
    keywords: ['reserva', 'reservar', 'booking', 'agendar', 'apartar'],
    response: 'Para reservar, ve a la sección "Viajes", elige tu destino o paquete y haz clic en "Reservar". Luego en tu dashboard podrás pagar con PSE, tarjeta o QR. ¿Necesitas ayuda con el proceso de pago?'
  },
  {
    keywords: ['pago', 'pagar', 'qr', 'pse', 'tarjeta', 'transferencia'],
    response: 'Aceptamos pagos con PSE, tarjeta de crédito/débito y código QR. El pago con QR es muy fácil: selecciona "Pagar con QR", escanea el código con tu celular y confirma. También puedes simular el escaneo desde la misma página. ¿Quieres saber más?'
  },
  {
    keywords: ['cancelar', 'cancelacion', 'devolucion', 'reembolso'],
    response: 'Puedes cancelar tus reservas desde tu dashboard en "Mis Reservas". Las cancelaciones con más de 48 horas de anticipación tienen reembolso completo. Si ya pagaste, el reembolso se procesa en 3-5 días hábiles.'
  },
  {
    keywords: ['contacto', 'telefono', 'email', 'correo', 'soporte', 'ayuda'],
    response: 'Puedes contactarnos en info@touristchain.com, llamar al +34 123 456 789 o usar el formulario en la sección "Contacto". Nuestro horario es de lunes a viernes de 9:00 a 18:00.'
  },
  {
    keywords: ['admin', 'administrador', 'panel admin', 'login admin'],
    response: 'El panel de administración está en admin.html. Puedes ingresar con admin@touristchain.com / admin123 (credenciales de demo). Desde allí puedes gestionar usuarios, empresas, pagos y viajes.'
  },
  {
    keywords: ['renta', 'carro', 'auto', 'vehiculo', 'suv'],
    response: 'Ofrecemos renta de carros en tres categorías: Básico (Toyota Yaris) $35/día, Medio (Honda CR-V) $65/día y Premium (BMW X5) $120/día. Incluyen seguro, KM ilimitados y opciones de GPS/WiFi.'
  },
  {
    keywords: ['recomendar', 'recomendacion', 'sugerencia', 'presupuesto'],
    response: 'Con gusto te recomiendo. Si tu presupuesto es menor a $1,000, te sugiero Cancún básico o renta de carro. Entre $1,000 y $2,000: Roma, Nueva York o paquetes medios. Más de $2,000: Tokio o paquete Black. ¿Cuál es tu presupuesto aproximado?'
  },
  {
    keywords: ['cancun', 'cancún', 'playa', 'caribe'],
    response: 'Cancún es uno de nuestros destinos estrella. Ofrecemos 5 días en resorts todo incluido con playas de arena blanca. Precios desde $799 en paquete básico hasta $2,499 en paquete Black. ¡Ideal para relax y diversión!'
  },
  {
    keywords: ['tokio', 'japon', 'japón'],
    response: 'Tokio es perfecto para quienes buscan tecnología y tradición. El paquete incluye 8 días en la capital japonesa, visitas a templos tradicionales, Akihabara y Shinjuku. Precio desde $1,800.'
  },
  {
    keywords: ['roma', 'italia'],
    response: 'Roma te espera con 6 días de historia viva: Coliseo, Vaticano, Fontana di Trevi y gastronomía inolvidable. Precio desde $1,100. ¡Una experiencia clásica e imperdible!'
  },
  {
    keywords: ['nueva york', 'new york', 'ny'],
    response: 'Nueva York: 4 días en la ciudad que nunca duerme. Broadway, museos, Times Square y mucho más. Precio desde $1,500. Perfecto para una escapada urbana.'
  },
  {
    keywords: ['bali', 'indonesia'],
    response: 'Bali ofrece 7 días de relax y cultura en playas paradisíacas de Indonesia. Precio desde $1,400. Ideal para desconectar y conectar con la naturaleza.'
  },
  {
    keywords: ['andes', 'montaña', 'trekking'],
    response: 'Aventura en los Andes: 7 días de trekking y cultura en la cordillera más larga del mundo. Precio desde $800. Perfecto para amantes de la naturaleza y el deporte.'
  },
  {
    keywords: ['gracias', 'thank', 'agradezco'],
    response: '¡De nada! Estoy aquí para lo que necesites. Que disfrutes tu experiencia con TouristChain. ✈️'
  },
  {
    keywords: ['adios', 'chao', 'hasta luego', 'nos vemos'],
    response: '¡Hasta luego! Que tengas un excelente viaje. Vuelve cuando quieras. 🌍'
  }
];

// Función principal de respuesta de la IA
function generateResponse(message, context = {}) {
  const lowerMsg = message.toLowerCase().trim();

  // Responder sobre el carrito actual si hay contexto
  if (context.reservations && context.reservations.length > 0) {
    if (lowerMsg.includes('carrito') || lowerMsg.includes('mis reservas') || lowerMsg.includes('total')) {
      const total = context.reservations.reduce((sum, r) => sum + (r.precio || 0), 0);
      const items = context.reservations.map(r => `• ${r.nombre} - $${r.precio}`).join('\n');
      return `Tienes ${context.reservations.length} item(s) en tu carrito por un total de $${total}:\n${items}\n\nPuedes ir al dashboard para pagar.`;
    }
  }

  // Buscar coincidencia por palabras clave
  for (const item of knowledgeBase) {
    for (const keyword of item.keywords) {
      if (lowerMsg.includes(keyword)) {
        return item.response;
      }
    }
  }

  // Respuestas contextuales avanzadas
  if (lowerMsg.includes('mejor') || lowerMsg.includes('popular')) {
    return 'Nuestros destinos más populares son Cancún, Tokio y Roma. El paquete más vendido es el Medio a $1,299 que incluye vuelo, hotel 4 estrellas, todo incluido y excursiones. ¿Te gustaría reservarlo?';
  }

  if (lowerMsg.includes('seguro') || lowerMsg.includes('seguridad')) {
    return 'En TouristChain tu seguridad es prioridad. Usamos tecnología de respaldo, pagos verificados y alianzas con proveedores certificados. Además, los paquetes Black incluyen seguro de viaje premium.';
  }

  if (lowerMsg.includes('descuento') || lowerMsg.includes('promocion') || lowerMsg.includes('oferta')) {
    return '¡Buena noticia! Los socios de TouristChain obtienen hasta 20% de descuento en servicios asociados. También tenemos precios especiales en paquetes combinados. ¿Te gustaría ser socio o ver beneficios?';
  }

  // Fallback inteligente
  return `Entiendo que dices: "${message}". Como asistente de TouristChain, te puedo ayudar con destinos, precios, reservas, pagos, renta de carros y más. ¿Podrías darme más detalles de lo que necesitas?`;
}

// POST /api/chat - endpoint del asistente IA
export function sendChatMessage(req, res) {
  try {
    const { message, context } = req.body;

    if (!message || typeof message !== 'string') {
      return res.status(400).json({ error: 'El mensaje es requerido' });
    }

    const reply = generateResponse(message, context || {});

    // Simular pequeño delay de procesamiento de IA
    setTimeout(() => {
      res.json({
        reply,
        timestamp: new Date().toISOString(),
        agent: 'TouristChain Assistant'
      });
    }, 600);
  } catch (error) {
    console.error('Error en chat IA:', error);
    res.status(500).json({ error: 'Error al procesar tu mensaje' });
  }
}

// GET /api/chat/suggestions - sugerencias rápidas
export function getChatSuggestions(req, res) {
  res.json({
    suggestions: [
      '¿Cuáles son los destinos disponibles?',
      '¿Cuánto cuesta un viaje a Cancún?',
      '¿Cómo pago con QR?',
      '¿Qué incluye el paquete Black?',
      'Recomiéndame un destino'
    ]
  });
}
