CREATE TABLE users
(
    id                BIGSERIAL    NOT NULL,
    slug              UUID         NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at        TIMESTAMPTZ,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    full_name         VARCHAR(150) NOT NULL,
    artistic_name     VARCHAR(100) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    phone             VARCHAR(20),
    profile_photo_url VARCHAR(500),
    bio               TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    user_type         VARCHAR(20)  NOT NULL DEFAULT 'DJ',
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_slug UNIQUE (slug),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_users_user_type CHECK (user_type IN ('DJ'))
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_slug ON users (slug);
CREATE INDEX idx_users_status ON users (status);
