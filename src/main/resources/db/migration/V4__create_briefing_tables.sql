CREATE TABLE agent_configs
(
    id            BIGSERIAL    NOT NULL,
    slug          UUID         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at    TIMESTAMPTZ,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    name          VARCHAR(100) NOT NULL,
    system_prompt TEXT         NOT NULL,
    model         VARCHAR(100) NOT NULL DEFAULT 'gpt-4o-mini',
    temperature   DECIMAL(3, 2) NOT NULL DEFAULT 0.7,
    max_tokens    INT          NOT NULL DEFAULT 1500,
    enabled_tools JSONB        NOT NULL DEFAULT '[]',
    CONSTRAINT pk_agent_configs PRIMARY KEY (id),
    CONSTRAINT uq_agent_configs_slug UNIQUE (slug),
    CONSTRAINT uq_agent_configs_name UNIQUE (name)
);

INSERT INTO agent_configs (slug, name, system_prompt, model, temperature, max_tokens, enabled_tools)
VALUES (gen_random_uuid(),
        'briefing_assistant',
        'Você é um assistente de briefing musical para o DJDesk. Sua missão é conversar com o cliente de forma natural e acolhedora, em português brasileiro, para entender a festa dele e coletar informações musicais estruturadas. Conduza a conversa progressivamente: comece com perguntas fáceis sobre a vibe geral, depois explore estilos, restrições, músicas especiais e momentos importantes. Faça UMA pergunta por vez. Se o cliente responder de forma vaga, use exemplos concretos para aprofundar. Quando tiver informações suficientes sobre vibe, estilos, restrições, músicas e momentos, mostre um resumo e peça confirmação. Após o cliente confirmar, use a ferramenta finalizeBriefing para encerrar. Nunca invente preferências que o cliente não expressou.',
        'gpt-4o-mini',
        0.7,
        1500,
        '["getEventContext","saveVibe","savePreferredStyles","saveForbiddenStyles","saveRequiredSong","saveForbiddenSong","saveEventMoment","finalizeBriefing"]');

CREATE TABLE briefing_sessions
(
    id           BIGSERIAL   NOT NULL,
    slug         UUID        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active       BOOLEAN     NOT NULL DEFAULT TRUE,
    updated_at   TIMESTAMPTZ,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),
    event_id     BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    CONSTRAINT pk_briefing_sessions PRIMARY KEY (id),
    CONSTRAINT uq_briefing_sessions_slug UNIQUE (slug),
    CONSTRAINT fk_briefing_sessions_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE INDEX idx_briefing_sessions_event_id ON briefing_sessions (event_id);
CREATE INDEX idx_briefing_sessions_status ON briefing_sessions (status);

CREATE TABLE briefing_messages
(
    id         BIGSERIAL   NOT NULL,
    slug       UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active     BOOLEAN     NOT NULL DEFAULT TRUE,
    session_id BIGINT      NOT NULL,
    role       VARCHAR(20) NOT NULL,
    content    TEXT        NOT NULL,
    CONSTRAINT pk_briefing_messages PRIMARY KEY (id),
    CONSTRAINT uq_briefing_messages_slug UNIQUE (slug),
    CONSTRAINT fk_briefing_messages_session FOREIGN KEY (session_id) REFERENCES briefing_sessions (id) ON DELETE CASCADE
);

CREATE INDEX idx_briefing_messages_session_id ON briefing_messages (session_id);

CREATE TABLE briefing_results
(
    id               BIGSERIAL   NOT NULL,
    slug             UUID        NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active           BOOLEAN     NOT NULL DEFAULT TRUE,
    updated_at       TIMESTAMPTZ,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    event_id         BIGINT      NOT NULL,
    summary          TEXT,
    guest_profile    TEXT,
    vibe             TEXT,
    preferred_styles JSONB,
    forbidden_styles JSONB,
    required_songs   JSONB,
    forbidden_songs  JSONB,
    moments          JSONB,
    generated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_briefing_results PRIMARY KEY (id),
    CONSTRAINT uq_briefing_results_slug UNIQUE (slug),
    CONSTRAINT uq_briefing_results_event UNIQUE (event_id),
    CONSTRAINT fk_briefing_results_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);
