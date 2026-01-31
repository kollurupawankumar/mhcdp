CREATE TABLE IF NOT EXISTS pipeline_outbox_event (
        id BIGSERIAL PRIMARY KEY,
        event_id UUID NOT NULL UNIQUE,
        run_id VARCHAR(255) NOT NULL REFERENCES pipeline_run(run_id),
        topic VARCHAR(255) NOT NULL,
        event_type VARCHAR(100) NOT NULL,
        payload JSONB NOT NULL,
        status VARCHAR(20) NOT NULL DEFAULT 'NEW',   -- NEW/SENT/FAILED
        attempts INT NOT NULL DEFAULT 0,
        last_error TEXT,
        created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
        sent_at TIMESTAMP WITHOUT TIME ZONE
    );

CREATE INDEX IF NOT EXISTS idx_pipeline_outbox_status_created
    ON pipeline_outbox_event(status, created_at);
