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
