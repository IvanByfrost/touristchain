-- Reservas y pagos, antes en el backend mock de Node.
-- La tabla booking del esquema base no se usaba: se adapta a los ítems que reserva el frontend
-- (viajes, hoteles, renta de vehículos y paquetes), que no siempre tienen socio ni fechas.

CREATE TABLE public.payment (
    payment_id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
    method VARCHAR(20) NOT NULL CHECK (method IN ('tarjeta', 'nequi', 'qr', 'pse')),
    status VARCHAR(20) NOT NULL DEFAULT 'pendiente' CHECK (status IN ('pendiente', 'completado', 'rechazado')),
    reference VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),
    qr_token VARCHAR(64) UNIQUE,
    paid_at TIMESTAMP(6) WITHOUT TIME ZONE,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT payment_pkey PRIMARY KEY (payment_id),
    CONSTRAINT payment_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_user_id ON public.payment(user_id);

COMMENT ON TABLE public.payment IS 'Pagos simulados (tarjeta, Nequi, PSE, QR) de las reservas';
COMMENT ON COLUMN public.payment.qr_token IS 'Token secreto de la URL de confirmación del pago QR';

ALTER TABLE public.booking
    ALTER COLUMN partner_id DROP NOT NULL,
    ALTER COLUMN start_date DROP NOT NULL,
    ALTER COLUMN end_date DROP NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL,
    ADD COLUMN item_type VARCHAR(20) NOT NULL CHECK (item_type IN ('viaje', 'hotel', 'renta', 'paquete')),
    ADD COLUMN item_ref VARCHAR(64) NOT NULL,
    ADD COLUMN item_name VARCHAR(150) NOT NULL,
    ADD COLUMN trip_id BIGINT REFERENCES public.trip(id) ON DELETE SET NULL,
    ADD COLUMN unit_price NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
    ADD COLUMN quantity INTEGER NOT NULL CHECK (quantity > 0),
    ADD COLUMN payment_id UUID REFERENCES public.payment(payment_id) ON DELETE SET NULL,
    ADD COLUMN paid_at TIMESTAMP(6) WITHOUT TIME ZONE;

CREATE INDEX idx_booking_tourist_id ON public.booking(tourist_id);
CREATE INDEX idx_booking_payment_id ON public.booking(payment_id);

COMMENT ON COLUMN public.booking.item_ref IS 'Id del ítem en su catálogo (viaje, hotel, vehículo) o plan del paquete';
COMMENT ON COLUMN public.booking.quantity IS 'Personas (viaje), noches (hotel), días (renta) o 1 (paquete)';
COMMENT ON COLUMN public.booking.unit_price IS 'Precio unitario fijado por el servidor al reservar';
