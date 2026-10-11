ALTER TABLE public.partner
    ADD COLUMN contact_name VARCHAR(100),
    ADD COLUMN address VARCHAR(150),
    ADD COLUMN city VARCHAR(100),
    ADD COLUMN logo_url VARCHAR(500),
    ADD COLUMN benefits TEXT;

COMMENT ON COLUMN public.partner.contact_name IS 'Persona de contacto del socio';
COMMENT ON COLUMN public.partner.logo_url IS 'Logo mostrado en la vitrina pública';
COMMENT ON COLUMN public.partner.benefits IS 'Beneficios de la vitrina, uno por línea';
