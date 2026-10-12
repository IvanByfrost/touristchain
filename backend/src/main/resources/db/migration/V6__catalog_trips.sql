-- Catálogo Kyoto: viajes (paquetes con fechas y cupos), antes en el backend mock de Node.

CREATE TABLE public.trip (
    id BIGSERIAL PRIMARY KEY,
    destination VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    available INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'disponible'
        CHECK (status IN ('disponible', 'reservado', 'completado', 'cancelado')),
    image VARCHAR(500),
    tags TEXT[] NOT NULL DEFAULT '{}',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    rating NUMERIC(2, 1) NOT NULL DEFAULT 0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT trip_dates_check CHECK (end_date >= start_date),
    CONSTRAINT trip_available_check CHECK (available BETWEEN 0 AND capacity)
);

CREATE INDEX idx_trip_status ON public.trip(status);

COMMENT ON TABLE public.trip IS 'Paquetes de viaje con fechas, precio total y cupos';
COMMENT ON COLUMN public.trip.available IS 'Cupos libres; lo descuentan las reservas';
