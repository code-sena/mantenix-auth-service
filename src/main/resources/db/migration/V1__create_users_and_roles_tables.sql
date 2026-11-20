-- V1: users, roles and user_roles tables
-- Owner of this schema: auth-service (ADR-004). Never edit this file once applied;
-- create a new migration (V3__...) instead.
-- Design: 09-microservices/services/02-auth-service/data-model.md

CREATE TABLE users (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    email            VARCHAR(255)  NOT NULL,
    name             VARCHAR(100)  NOT NULL,          -- required by RegisterRequest in auth-service.yaml
    password_hash    VARCHAR(255)  NOT NULL,          -- bcrypt, never the raw password
    email_verified   BOOLEAN       NOT NULL DEFAULT FALSE,
    failed_attempts  INTEGER       NOT NULL DEFAULT 0,
    locked_until     TIMESTAMPTZ,                     -- NULL = not locked
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    deleted_at       TIMESTAMPTZ,                     -- soft delete: NULL = active

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_failed_attempts CHECK (failed_attempts >= 0)
);

CREATE INDEX idx_users_deleted_at ON users (deleted_at);

CREATE TABLE roles (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(50)   NOT NULL,
    description  TEXT,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id      UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id      UUID          NOT NULL REFERENCES roles (id) ON DELETE RESTRICT,
    assigned_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    assigned_by  UUID          REFERENCES users (id),   -- NULL until authentication exists

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id)
);
