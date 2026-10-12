-- Funciones que solo existían en los módulos legacy (athenas, newyork, ginebra),
-- adaptadas al esquema del monolito.

-- Athenas: respuesta del socio y verificación de reseñas por un administrador
ALTER TABLE public.review
    ADD COLUMN response TEXT,
    ADD COLUMN responded_at TIMESTAMP(6) WITHOUT TIME ZONE,
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN public.review.response IS 'Respuesta pública del socio (o de un administrador) a la reseña';
COMMENT ON COLUMN public.review.verified IS 'Reseña revisada y verificada por un administrador';

-- NewYork: el dinero de un pago completado queda retenido hasta liberarlo al socio o reembolsarlo
ALTER TABLE public.payment
    ADD COLUMN escrow_status VARCHAR(20) CHECK (escrow_status IN ('retenido', 'liberado', 'reembolsado')),
    ADD COLUMN released_at TIMESTAMP(6) WITHOUT TIME ZONE,
    ADD COLUMN refunded_at TIMESTAMP(6) WITHOUT TIME ZONE;

UPDATE public.payment SET escrow_status = 'retenido' WHERE status = 'completado';

CREATE INDEX idx_payment_escrow_status ON public.payment(escrow_status);

COMMENT ON COLUMN public.payment.escrow_status IS 'Custodia del pago completado: retenido, liberado al socio o reembolsado';

-- Ginebra: verificación de socios por un administrador
ALTER TABLE public.partner
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN verified_at TIMESTAMP(6) WITHOUT TIME ZONE;

COMMENT ON COLUMN public.partner.verified IS 'Socio verificado por un administrador (documentación y NIT revisados)';
