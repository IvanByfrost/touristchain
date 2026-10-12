-- Los destinos se muestran en el mapa y cada viaje (paquete) puede pertenecer a un destino.

ALTER TABLE public.destinations
    ADD COLUMN latitude DOUBLE PRECISION,
    ADD COLUMN longitude DOUBLE PRECISION;

ALTER TABLE public.trip
    ADD COLUMN destination_id BIGINT REFERENCES public.destinations(id) ON DELETE SET NULL;

CREATE INDEX idx_trip_destination_id ON public.trip(destination_id);

COMMENT ON COLUMN public.trip.destination_id IS 'Destino del catálogo al que pertenece el paquete (opcional)';
