-- Script de creación de base de datos para User API
-- Base de datos: H2 (en memoria)

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    last_login TIMESTAMP NOT NULL,
    token TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de teléfonos
CREATE TABLE IF NOT EXISTS phones (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    number VARCHAR(255) NOT NULL,
    citycode VARCHAR(255) NOT NULL,
    contrycode VARCHAR(255) NOT NULL,
    user_id UUID,
    CONSTRAINT fk_phone_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_phones_user_id ON phones(user_id);

-- Comentarios sobre las tablas
COMMENT ON TABLE users IS 'Tabla principal de usuarios del sistema';
COMMENT ON TABLE phones IS 'Tabla de teléfonos asociados a usuarios';

-- Comentarios sobre columnas importantes
COMMENT ON COLUMN users.email IS 'Email único del usuario, usado para login';
COMMENT ON COLUMN users.token IS 'Token JWT para autenticación';
COMMENT ON COLUMN users.is_active IS 'Indica si el usuario está activo en el sistema';
COMMENT ON COLUMN phones.citycode IS 'Código de ciudad del teléfono';
COMMENT ON COLUMN phones.contrycode IS 'Código de país del teléfono';