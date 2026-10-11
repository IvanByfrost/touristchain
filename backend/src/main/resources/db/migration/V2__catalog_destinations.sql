CREATE TABLE public.destinations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price_per_day DOUBLE PRECISION,
    currency VARCHAR(255) NOT NULL DEFAULT 'USD',
    rating DOUBLE PRECISION DEFAULT 0,
    total_reviews INTEGER DEFAULT 0,
    contract_address VARCHAR(255),
    provider_wallet VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.destination_images (
    destination_id BIGINT NOT NULL REFERENCES public.destinations(id) ON DELETE CASCADE,
    image_url VARCHAR(255)
);

CREATE TABLE public.destination_tags (
    destination_id BIGINT NOT NULL REFERENCES public.destinations(id) ON DELETE CASCADE,
    tag VARCHAR(255)
);

CREATE INDEX idx_destinations_country ON public.destinations(country);
CREATE INDEX idx_destinations_category ON public.destinations(category);
CREATE INDEX idx_destinations_active ON public.destinations(active);
CREATE INDEX idx_destination_images_destination_id ON public.destination_images(destination_id);
CREATE INDEX idx_destination_tags_destination_id ON public.destination_tags(destination_id);
