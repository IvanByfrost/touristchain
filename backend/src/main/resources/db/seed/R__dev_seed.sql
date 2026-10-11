INSERT INTO public.users (user_id, first_name, last_name, email, phone, password_hash, status_account, status_profile)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Administrador', 'Principal', 'admin@touristchain.com', '+57 300 000 0001',
     convert_to('$2b$10$CG.PcrrifGHvxHRgsT3LPeTG.dOySz7wTwbQ/kaRmojIZflftSsuq', 'UTF8'), 'active', 'active'),
    ('00000000-0000-0000-0000-000000000002', 'Juan', 'Pérez', 'juan@example.com', '+57 300 000 0002',
     convert_to('$2b$10$CG.PcrrifGHvxHRgsT3LPeTG.dOySz7wTwbQ/kaRmojIZflftSsuq', 'UTF8'), 'active', 'active'),
    ('00000000-0000-0000-0000-000000000003', 'Caribe', 'Real', 'socio@caribereal.com', '+57 300 000 0003',
     convert_to('$2b$10$qDcK5l/lys8L7pc9Q5HZluNOdOayZ70yMR6xzk6ATTEMov.pljUu2', 'UTF8'), 'active', 'active')
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.administrator (user_id, admin_level)
SELECT '00000000-0000-0000-0000-000000000001', 'SUPERADMIN'
WHERE NOT EXISTS (SELECT 1 FROM public.administrator WHERE user_id = '00000000-0000-0000-0000-000000000001');

INSERT INTO public.tourist (user_id, loyalty_points)
SELECT '00000000-0000-0000-0000-000000000002', 250
WHERE NOT EXISTS (SELECT 1 FROM public.tourist WHERE user_id = '00000000-0000-0000-0000-000000000002');

INSERT INTO public.partner (user_id, company_name, tax_id, business_type, company_description, contact_email, contact_phone)
SELECT '00000000-0000-0000-0000-000000000003', 'Caribe Real S.A.S.', '900123456-7', 'hotel',
       'Operador hotelero en el Caribe colombiano', 'socio@caribereal.com', '+57 300 000 0003'
WHERE NOT EXISTS (SELECT 1 FROM public.partner WHERE user_id = '00000000-0000-0000-0000-000000000003');

UPDATE public.partner
SET contact_name = 'María Gómez',
    city = 'Cartagena',
    website = 'https://example.com/caribereal',
    benefits = E'Descuento 15% para viajeros TouristChain\nDesayuno incluido\nUpgrade de habitación sujeto a disponibilidad'
WHERE user_id = '00000000-0000-0000-0000-000000000003' AND benefits IS NULL;

INSERT INTO public.review (tourist_id, item_type, item_ref, item_name, rating, comment)
SELECT t.tourist_id, 'viaje', '1', 'París, Francia', 5, 'Experiencia inolvidable, todo muy bien organizado.'
FROM public.tourist t
WHERE t.user_id = '00000000-0000-0000-0000-000000000002'
  AND NOT EXISTS (SELECT 1 FROM public.review r WHERE r.tourist_id = t.tourist_id AND r.item_ref = '1');

INSERT INTO public.destinations (name, description, country, city, category, price_per_day, rating, total_reviews)
SELECT v.name, v.description, v.country, v.city, v.category, v.price, v.rating, v.reviews
FROM (VALUES
    ('París', 'Tour por la ciudad del amor con visita a la Torre Eiffel, Louvre y paseo en barco por el Sena', 'Francia', 'París', 'CITY', 170.0, 4.8, 12),
    ('Roma', 'Historia y cultura en la Ciudad Eterna con visitas al Coliseo, Vaticano y Fontana di Trevi', 'Italia', 'Roma', 'CULTURE', 157.0, 4.7, 9),
    ('Tokio', 'Aventura tecnológica y cultural en la capital japonesa', 'Japón', 'Tokio', 'CITY', 220.0, 4.9, 15),
    ('Cancún', 'Resorts todo incluido con playas de arena blanca y mar caribe', 'México', 'Cancún', 'BEACH', 240.0, 4.6, 22),
    ('Cartagena', 'Ciudad amurallada, playas de Barú y atardeceres en el Caribe', 'Colombia', 'Cartagena', 'BEACH', 120.0, 4.7, 18),
    ('Bali', 'Relax y cultura en las paradisíacas islas de Indonesia', 'Indonesia', 'Bali', 'BEACH', 140.0, 4.8, 11)
) AS v(name, description, country, city, category, price, rating, reviews)
WHERE NOT EXISTS (SELECT 1 FROM public.destinations d WHERE d.name = v.name);

INSERT INTO public.destination_images (destination_id, image_url)
SELECT d.id, v.url
FROM (VALUES
    ('París', 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34'),
    ('Roma', 'https://images.unsplash.com/photo-1552832230-c0197dd311b5'),
    ('Tokio', 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf'),
    ('Cancún', 'https://images.unsplash.com/photo-1511895426328-dc8714191300'),
    ('Cartagena', 'https://images.unsplash.com/photo-1583531352515-8884af319dc1'),
    ('Bali', 'https://images.unsplash.com/photo-1537996194471-e657df975ab4')
) AS v(name, url)
JOIN public.destinations d ON d.name = v.name
WHERE NOT EXISTS (SELECT 1 FROM public.destination_images i WHERE i.destination_id = d.id);

INSERT INTO public.destination_tags (destination_id, tag)
SELECT d.id, v.tag
FROM (VALUES
    ('París', 'romántico'), ('París', 'cultura'), ('París', 'europa'),
    ('Roma', 'historia'), ('Roma', 'cultura'), ('Roma', 'gastronomía'),
    ('Tokio', 'tecnología'), ('Tokio', 'cultura'), ('Tokio', 'asia'),
    ('Cancún', 'playa'), ('Cancún', 'relax'), ('Cancún', 'caribe'),
    ('Cartagena', 'playa'), ('Cartagena', 'historia'), ('Cartagena', 'caribe'),
    ('Bali', 'playa'), ('Bali', 'naturaleza'), ('Bali', 'asia')
) AS v(name, tag)
JOIN public.destinations d ON d.name = v.name
WHERE NOT EXISTS (SELECT 1 FROM public.destination_tags t WHERE t.destination_id = d.id AND t.tag = v.tag);

INSERT INTO public.hotel (name, city, stars, price, image, description, amenities, latitude, longitude)
SELECT v.name, v.city, v.stars, v.price, v.image, v.description, v.amenities, v.lat, v.lng
FROM (VALUES
    (1, 'Marriott Champs-Élysées', 'París, Francia', 5, 280, 'https://images.unsplash.com/photo-1566073771259-6a8506099945',
     'Hotel de lujo con vista a la Torre Eiffel y servicio premium.', ARRAY['WiFi', 'Spa', 'Piscina', 'Gimnasio', 'Restaurante'], 48.8738, 2.295),
    (2, 'Hotel Artis', 'Roma, Italia', 4, 150, 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa',
     'Hotel boutique cerca del Coliseo con encanto italiano.', ARRAY['WiFi', 'Desayuno', 'Terraza', 'Aire acondicionado'], 41.89, 12.49),
    (3, 'Park Hyatt Tokyo', 'Tokio, Japón', 5, 450, 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb',
     'Hotel icónico con vistas panorámicas de Tokio y monte Fuji.', ARRAY['WiFi', 'Spa', 'Piscina', 'Gimnasio', 'Bar'], 35.685, 139.69),
    (4, 'The Westin Cancún', 'Cancún, México', 4, 220, 'https://images.unsplash.com/photo-1571896349842-33c89424de2d',
     'Resort frente al mar con playa privada y actividades acuáticas.', ARRAY['WiFi', 'Playa', 'Piscina', 'Restaurante', 'Bar'], 21.14, -86.77),
    (5, 'The Plaza New York', 'Nueva York, USA', 5, 520, 'https://images.unsplash.com/photo-1564501049412-61c2a3083791',
     'Hotel histórico en Fifth Avenue, símbolo de lujo neoyorquino.', ARRAY['WiFi', 'Spa', 'Gimnasio', 'Restaurante', 'Concierge'], 40.7647, -73.9748),
    (6, 'Ayana Resort Bali', 'Bali, Indonesia', 5, 350, 'https://images.unsplash.com/photo-1537996194471-e657df975ab4',
     'Resort de lujo con villas privadas y vistas al océano Índico.', ARRAY['WiFi', 'Spa', 'Piscina infinita', 'Playa', 'Restaurante'], -8.79, 115.21),
    (7, 'JW Marriott Bogotá', 'Bogotá, Colombia', 5, 180, 'https://images.unsplash.com/photo-1618773928121-c32242e63f39',
     'Hotel moderno en el corazón financiero de Bogotá.', ARRAY['WiFi', 'Spa', 'Gimnasio', 'Restaurante', 'Bar'], 4.68, -74.05),
    (8, 'Hotel Barcelona Center', 'Barcelona, España', 4, 160, 'https://images.unsplash.com/photo-1563911302283-d2bc129e7c1f',
     'Elegante hotel cerca de Paseo de Gracia y La Sagrada Familia.', ARRAY['WiFi', 'Terraza', 'Desayuno', 'Gimnasio'], 41.39, 2.16)
) AS v(ord, name, city, stars, price, image, description, amenities, lat, lng)
WHERE NOT EXISTS (SELECT 1 FROM public.hotel h WHERE h.name = v.name)
ORDER BY v.ord;

INSERT INTO public.rental_car (name, type, category, price, image, features, rating, total_reviews)
SELECT v.name, v.type, v.category, v.price, v.image, v.features, v.rating, v.reviews
FROM (VALUES
    (1, 'Toyota Yaris', 'Compacto', 'basico', 35, 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2',
     ARRAY['5 personas', 'Automático', 'Aire acondicionado', 'Bluetooth'], 4.5, 8),
    (2, 'Honda CR-V', 'SUV', 'medio', 65, 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341',
     ARRAY['7 personas', 'Automático', 'GPS', 'WiFi', 'Cámara trasera'], 4.7, 12),
    (3, 'BMW X5', 'Premium', 'black', 120, 'https://images.unsplash.com/photo-1555212697-194d092e3b8f',
     ARRAY['5 personas', 'Automático', 'Pantalla táctil', 'Asientos de cuero', 'Sistema premium'], 4.9, 6)
) AS v(ord, name, type, category, price, image, features, rating, reviews)
WHERE NOT EXISTS (SELECT 1 FROM public.rental_car c WHERE c.name = v.name)
ORDER BY v.ord;

INSERT INTO public.company (ruc, name, contact, email, phone, address, status, workers)
VALUES
    ('12345678901', 'Viajes del Sol S.A.', 'Roberto Gómez', 'info@viajessol.com', '+34 900 123 456',
     'Av. del Turismo 123, Barcelona', 'activa', 5),
    ('98765432109', 'Aventuras Extremas S.L.', 'Laura Méndez', 'contacto@aventuras.com', '+34 900 654 321',
     'Calle Aventura 45, Madrid', 'activa', 8)
ON CONFLICT (ruc) DO NOTHING;
