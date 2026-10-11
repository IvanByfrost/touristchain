-- Catálogo Kyoto: hoteles, vehículos de alquiler y empresas asociadas
-- (antes vivían en el backend mock de Node).

CREATE TABLE public.hotel (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(150) NOT NULL,
    stars INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5),
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    image VARCHAR(500),
    description TEXT,
    amenities TEXT[] NOT NULL DEFAULT '{}',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hotel_city ON public.hotel(city);

CREATE TABLE public.rental_car (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(30) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    image VARCHAR(500),
    features TEXT[] NOT NULL DEFAULT '{}',
    rating NUMERIC(2, 1) NOT NULL DEFAULT 0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rental_car_category ON public.rental_car(category);

COMMENT ON COLUMN public.rental_car.category IS 'Gama del vehículo: basico, medio, black';

CREATE TABLE public.company (
    id BIGSERIAL PRIMARY KEY,
    ruc VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    contact VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'activa' CHECK (status IN ('activa', 'inactiva', 'pendiente')),
    workers INTEGER NOT NULL DEFAULT 0 CHECK (workers >= 0),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.company IS 'Empresas asociadas gestionadas por el administrador';
COMMENT ON COLUMN public.company.ruc IS 'Identificación tributaria (RUC / NIT)';
