CREATE TABLE events
(
    id               BIGSERIAL    NOT NULL,
    slug             UUID         NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at       TIMESTAMPTZ,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    name             VARCHAR(200) NOT NULL,
    client_name      VARCHAR(150) NOT NULL,
    type             VARCHAR(100),
    status           VARCHAR(50)  NOT NULL DEFAULT 'CREATED',
    public_link      UUID         NOT NULL,
    event_date       TIMESTAMPTZ,
    additional_info  JSONB,
    dj_id            BIGINT       NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT uq_events_slug UNIQUE (slug),
    CONSTRAINT uq_events_public_link UNIQUE (public_link),
    CONSTRAINT fk_events_dj_id FOREIGN KEY (dj_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_events_dj_id ON events (dj_id);
CREATE INDEX idx_events_slug ON events (slug);
CREATE INDEX idx_events_status ON events (status);
CREATE INDEX idx_events_event_date ON events (event_date DESC);
