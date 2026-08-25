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

CREATE TABLE public.tourist (
    -- Identificador principal
    tourist_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Relación con el usuario base
    user_id UUID NOT NULL REFERENCES public.users(user_id) ON DELETE CASCADE,
    
    -- Auditoría
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Campos específicos del turista
    preferred_language VARCHAR(10) DEFAULT 'es',
    loyalty_points INTEGER DEFAULT 0,
    travel_preferences JSONB,  -- Preferencias de viaje en formato JSON (ej. { "accommodation": "hotel", "activities": ["museums", "beach"] })
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    newsletter_subscription BOOLEAN DEFAULT TRUE,
    
    --Restricciones CHECK
    CONSTRAINT tourist_loyalty_points_check CHECK (loyalty_points >= 0),
    CONSTRAINT tourist_preferred_language_check CHECK (preferred_language IN ('es', 'en', 'fr', 'de', 'pt', 'it', 'ja', 'zh'))
);

-- 📇 Índices
CREATE INDEX idx_tourist_user_id ON public.tourist(user_id);
CREATE INDEX idx_tourist_loyalty_points ON public.tourist(loyalty_points) WHERE loyalty_points > 0;