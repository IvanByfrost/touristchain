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