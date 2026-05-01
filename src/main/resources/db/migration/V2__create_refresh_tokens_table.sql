CREATE TABLE refresh_tokens
(
    id         BIGSERIAL    NOT NULL,
    slug       UUID         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    token      VARCHAR(500) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    user_id    BIGINT       NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_slug UNIQUE (slug),
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens (revoked) WHERE revoked = FALSE;
