-- Reseñas sobre ítems del catálogo (viajes, hoteles, autos, paquetes) que todavía
-- no tienen tabla propia: se guarda el tipo, la referencia y el nombre mostrado.

ALTER TABLE public.review
    ADD COLUMN item_type VARCHAR(20),
    ADD COLUMN item_ref VARCHAR(64),
    ADD COLUMN item_name VARCHAR(150);

ALTER TABLE public.review DROP CONSTRAINT review_target_check;
ALTER TABLE public.review ADD CONSTRAINT review_target_check CHECK (
    partner_id IS NOT NULL
    OR package_id IS NOT NULL
    OR (item_type IS NOT NULL AND item_ref IS NOT NULL)
);

-- Un turista reseña cada ítem una sola vez
CREATE UNIQUE INDEX uq_review_tourist_item ON public.review(tourist_id, item_type, item_ref)
    WHERE item_ref IS NOT NULL;
CREATE INDEX idx_review_item ON public.review(item_type, item_ref);

COMMENT ON COLUMN public.review.item_type IS 'Tipo de ítem reseñado: viaje, hotel, car, renta, paquete';
COMMENT ON COLUMN public.review.item_ref IS 'Identificador del ítem en su catálogo de origen';
COMMENT ON COLUMN public.review.item_name IS 'Nombre del ítem al momento de la reseña';
