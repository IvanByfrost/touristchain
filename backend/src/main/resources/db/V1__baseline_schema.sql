CREATE TABLE public.users (
    -- Identificador principal (UUID)
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Auditoría (estándar en todas las tablas)
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Datos personales
    first_name VARCHAR(50) NOT NULL,
    second_name VARCHAR(50),
    last_name VARCHAR(60) NOT NULL,
    second_last_name VARCHAR(60),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    blood_type VARCHAR(10),
    
    -- Identificación
    credential_type VARCHAR(20) NOT NULL,
    credential_number VARCHAR(64) NOT NULL UNIQUE, -- Único a nivel global
    
    -- Contacto
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(100),
    
    -- 🔐 Seguridad y autenticación
    password_hash BYTEA NOT NULL, -- Hash de la contraseña (BCrypt o similar)
    failed_attempts SMALLINT DEFAULT 0,
    locked_until TIMESTAMP(6) WITHOUT TIME ZONE,
    lock_reason VARCHAR(500),
        
    -- Estado de la cuenta y perfil
    status_account VARCHAR(20) NOT NULL DEFAULT 'pending',
    status_profile VARCHAR(20) DEFAULT 'pending_profile',
    
    -- Restricciones CHECK (manteniendo el estilo que me diste)
    CONSTRAINT users_blood_type_check CHECK (blood_type IN ('A_POS', 'A_NEG', 'B_POS', 'B_NEG', 'AB_POS', 'AB_NEG', 'O_POS', 'O_NEG', 'UNKNOWN')),
    CONSTRAINT users_credential_type_check CHECK (credential_type IN ('CC', 'TI', 'CE', 'PPT', 'PEP', 'PAS')),
    CONSTRAINT users_gender_check CHECK (gender IN ('M', 'F', 'OTHER', 'NOT_SPECIFIED')),
    CONSTRAINT users_status_account_check CHECK (status_account IN ('pending', 'active', 'locked', 'suspended', 'disabled', 'reset_required', 'change_required')),
    CONSTRAINT users_status_profile_check CHECK (status_profile IN ('pending_profile', 'active', 'blocked'))
);

-- 📇 Índices para búsquedas rápidas
CREATE INDEX idx_users_email ON public.heimdall_users(email);
CREATE INDEX idx_users_credential_number ON public.heimdall_users(credential_number);
CREATE INDEX idx_users_status_account ON public.heimdall_users(status_account);
CREATE INDEX idx_users_date_of_birth ON public.heimdall_users(date_of_birth);

-- ======================================================
-- Tabla: tourist
-- Descripción: Almacena información específica de los turistas
-- Relación: 1:1 con users (user_id)
-- ======================================================

CREATE TABLE public.tourist (
    -- 🔑 Identificador principal (UUID)
    tourist_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- 🔗 Relación con el usuario base
    user_id UUID NOT NULL,
    
    -- 📅 Auditoría (estándar en todas las tablas)
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 🎯 Campos específicos del turista
    preferred_language VARCHAR(10) DEFAULT 'es',
    loyalty_points INTEGER DEFAULT 0,
    travel_preferences JSONB,  -- Almacena preferencias en JSON
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    newsletter_subscription BOOLEAN DEFAULT TRUE,
    
    -- ✅ Restricciones de integridad
    CONSTRAINT tourist_pkey PRIMARY KEY (tourist_id),
    CONSTRAINT tourist_user_id_fkey FOREIGN KEY (user_id) 
        REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT tourist_loyalty_points_check CHECK (loyalty_points >= 0),
    CONSTRAINT tourist_preferred_language_check CHECK (
        preferred_language IN ('es', 'en', 'fr', 'de', 'pt', 'it', 'ja', 'zh')
    )
);

-- 📇 Índices para mejorar el rendimiento
CREATE INDEX idx_tourist_user_id ON public.tourist(user_id);
CREATE INDEX idx_tourist_loyalty_points ON public.tourist(loyalty_points) WHERE loyalty_points > 0;
CREATE INDEX idx_tourist_newsletter ON public.tourist(newsletter_subscription) WHERE newsletter_subscription = TRUE;
CREATE INDEX idx_tourist_travel_preferences ON public.tourist USING GIN (travel_preferences);

-- 📝 Comentarios para documentación
COMMENT ON TABLE public.tourist IS 'Perfiles de turistas, extendiendo la información de users';
COMMENT ON COLUMN public.tourist.preferred_language IS 'Idioma preferido para comunicaciones (ej. es, en, fr)';
COMMENT ON COLUMN public.tourist.loyalty_points IS 'Puntos acumulados en el programa de fidelidad';
COMMENT ON COLUMN public.tourist.travel_preferences IS 'Preferencias de viaje en formato JSON (ej. alojamiento, actividades)';
COMMENT ON COLUMN public.tourist.emergency_contact_name IS 'Nombre del contacto de emergencia';
COMMENT ON COLUMN public.tourist.emergency_contact_phone IS 'Teléfono del contacto de emergencia';
COMMENT ON COLUMN public.tourist.newsletter_subscription IS 'Indica si el turista acepta recibir boletines informativos';

-- ======================================================
-- Tabla: administrator
-- Descripción: Almacena información específica de los administradores
-- Relación: 1:1 con users (user_id)
-- ======================================================

CREATE TABLE public.administrator (
    -- 🔑 Identificador principal (UUID)
    administrator_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- 🔗 Relación con el usuario base
    user_id UUID NOT NULL,
    
    -- 📅 Auditoría (estándar en todas las tablas)
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 🎯 Campos específicos del administrador
    admin_level VARCHAR(20) DEFAULT 'MODERATOR',
    
    -- ✅ Restricciones de integridad
    CONSTRAINT administrator_pkey PRIMARY KEY (administrator_id),
    CONSTRAINT administrator_user_id_fkey FOREIGN KEY (user_id) 
        REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT administrator_admin_level_check CHECK (
        admin_level IN ('SUPERADMIN', 'MODERATOR', 'SUPPORT')
    )
);

-- 📇 Índices para mejorar el rendimiento
CREATE INDEX idx_administrator_user_id ON public.administrator(user_id);
CREATE INDEX idx_administrator_admin_level ON public.administrator(admin_level);

-- 📝 Comentarios para documentación
COMMENT ON TABLE public.administrator IS 'Perfiles de administradores, extendiendo la información de users';
COMMENT ON COLUMN public.administrator.admin_level IS 'Nivel de acceso: SUPERADMIN, MODERATOR, SUPPORT';

-- ======================================================
-- Tabla: partner
-- Descripción: Almacena información específica de los proveedores (empresas, agencias, hoteles, etc.)
-- Relación: 1:1 con users (user_id)
-- ======================================================

CREATE TABLE public.partner (
    -- 🔑 Identificador principal (UUID)
    partner_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- 🔗 Relación con el usuario base
    user_id UUID NOT NULL,
    
    -- 📅 Auditoría (estándar en todas las tablas)
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- 🏢 Datos de la empresa
    company_name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(20) UNIQUE,
    business_type VARCHAR(50),
    company_description TEXT,
    website VARCHAR(200),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    
    -- 📍 Ubicación (relación con geografía)
    country_id UUID,  -- Opcional, se validará cuando exista la tabla country
    
    -- ✅ Restricciones de integridad
    CONSTRAINT partner_pkey PRIMARY KEY (partner_id),
    CONSTRAINT partner_user_id_fkey FOREIGN KEY (user_id) 
        REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT partner_country_id_fkey FOREIGN KEY (country_id) 
        REFERENCES public.country(country_id) ON DELETE SET NULL
);

-- 📇 Índices para mejorar el rendimiento
CREATE INDEX idx_partner_user_id ON public.partner(user_id);
CREATE INDEX idx_partner_company_name ON public.partner(company_name);
CREATE INDEX idx_partner_tax_id ON public.partner(tax_id);
CREATE INDEX idx_partner_business_type ON public.partner(business_type);
CREATE INDEX idx_partner_country_id ON public.partner(country_id);

-- 📝 Comentarios para documentación
COMMENT ON TABLE public.partner IS 'Perfiles de proveedores, extendiendo la información de users';
COMMENT ON COLUMN public.partner.company_name IS 'Nombre legal de la empresa o proveedor';
COMMENT ON COLUMN public.partner.tax_id IS 'Número de identificación tributaria (NIT, RUC, etc.)';
COMMENT ON COLUMN public.partner.business_type IS 'Tipo de negocio: HOTEL, AGENCY, TRANSPORT, RESTAURANT, etc.';
COMMENT ON COLUMN public.partner.company_description IS 'Descripción breve de la empresa';
COMMENT ON COLUMN public.partner.website IS 'Sitio web del proveedor';
COMMENT ON COLUMN public.partner.contact_email IS 'Email de contacto del proveedor';
COMMENT ON COLUMN public.partner.contact_phone IS 'Teléfono de contacto del proveedor';
COMMENT ON COLUMN public.partner.country_id IS 'País de operación del proveedor';

-- ======================================================
-- Tabla: review
-- Descripción: Almacena las valoraciones que los turistas 
--              hacen sobre proveedores, servicios o paquetes.
-- Relación: N:1 con tourist, N:1 con partner, N:1 con package (opcional)
-- ======================================================

CREATE TABLE public.review (
    -- 🔑 Identificador principal (UUID)
    review_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- 🔗 Relaciones
    tourist_id UUID NOT NULL,
    partner_id UUID,                        -- Puede ser NULL si es sobre un paquete
    package_id UUID,                        -- Puede ser NULL si es sobre un partner
    booking_id UUID,                        -- Opcional: referencia a la reserva asociada
    
    -- ⭐ Valoración
    rating INTEGER NOT NULL,
    comment TEXT,
    
    -- 📅 Auditoría
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- ✅ Restricciones
    CONSTRAINT review_pkey PRIMARY KEY (review_id),
    CONSTRAINT review_tourist_id_fkey FOREIGN KEY (tourist_id) 
        REFERENCES public.tourist(tourist_id) ON DELETE CASCADE,
    CONSTRAINT review_partner_id_fkey FOREIGN KEY (partner_id) 
        REFERENCES public.partner(partner_id) ON DELETE CASCADE,
    CONSTRAINT review_package_id_fkey FOREIGN KEY (package_id) 
        REFERENCES public.package(package_id) ON DELETE CASCADE,
    CONSTRAINT review_booking_id_fkey FOREIGN KEY (booking_id) 
        REFERENCES public.booking(booking_id) ON DELETE SET NULL,
    CONSTRAINT review_rating_check CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT review_target_check CHECK (
        (partner_id IS NOT NULL AND package_id IS NULL) OR
        (partner_id IS NULL AND package_id IS NOT NULL) OR
        (partner_id IS NOT NULL AND package_id IS NOT NULL)
    )
);

-- 📇 Índices
CREATE INDEX idx_review_tourist_id ON public.review(tourist_id);
CREATE INDEX idx_review_partner_id ON public.review(partner_id);
CREATE INDEX idx_review_package_id ON public.review(package_id);
CREATE INDEX idx_review_booking_id ON public.review(booking_id);
CREATE INDEX idx_review_rating ON public.review(rating);
CREATE INDEX idx_review_created_at ON public.review(created_at);

-- 📝 Comentarios
COMMENT ON TABLE public.review IS 'Valoraciones de turistas sobre proveedores o paquetes';
COMMENT ON COLUMN public.review.rating IS 'Puntuación de 1 a 5 estrellas';
COMMENT ON COLUMN public.review.comment IS 'Comentario opcional del turista';

-- ======================================================
-- Tabla: reputation_summary
-- Descripción: Resumen de reputación por actor (turista o partner)
-- ======================================================

CREATE TABLE public.reputation_summary (
    -- 🔑 Identificador
    reputation_summary_id UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- 🎯 Actor
    target_type VARCHAR(20) NOT NULL,  -- 'TOURIST' o 'PARTNER'
    target_id UUID NOT NULL,
    
    -- 📊 Estadísticas
    average_rating DECIMAL(3,2) DEFAULT 0,
    total_reviews INTEGER DEFAULT 0,
    rating_1_count INTEGER DEFAULT 0,
    rating_2_count INTEGER DEFAULT 0,
    rating_3_count INTEGER DEFAULT 0,
    rating_4_count INTEGER DEFAULT 0,
    rating_5_count INTEGER DEFAULT 0,
    
    -- 📅 Última actualización
    last_updated TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- ✅ Restricciones
    CONSTRAINT reputation_summary_pkey PRIMARY KEY (reputation_summary_id),
    CONSTRAINT reputation_summary_unique_target UNIQUE (target_type, target_id),
    CONSTRAINT reputation_summary_target_type_check CHECK (target_type IN ('TOURIST', 'PARTNER'))
);

-- 📇 Índices
CREATE INDEX idx_reputation_summary_target ON public.reputation_summary(target_type, target_id);
CREATE INDEX idx_reputation_summary_avg_rating ON public.reputation_summary(average_rating);

CREATE TABLE public.booking (
    booking_id UUID NOT NULL DEFAULT gen_random_uuid(),
    tourist_id UUID NOT NULL,
    partner_id UUID NOT NULL,
    package_id UUID,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT booking_pkey PRIMARY KEY (booking_id),
    CONSTRAINT booking_tourist_id_fkey FOREIGN KEY (tourist_id)
        REFERENCES public.tourist(tourist_id) ON DELETE CASCADE,
    CONSTRAINT booking_partner_id_fkey FOREIGN KEY (partner_id)
        REFERENCES public.partner(partner_id) ON DELETE CASCADE,
    CONSTRAINT booking_package_id_fkey FOREIGN KEY (package_id)
        REFERENCES public.package(package_id) ON DELETE SET NULL,
    CONSTRAINT booking_status_check CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'))
);