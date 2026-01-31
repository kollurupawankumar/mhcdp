-- ==========================================
-- 02_runtime.sql
-- RUNTIME TABLES (CREATE + INSERT)
-- ==========================================

-- ------------------------
-- PIPELINE RUN
-- ------------------------
CREATE TABLE IF NOT EXISTS pipeline_run (
                                            id BIGSERIAL PRIMARY KEY,
                                            run_id VARCHAR(255) UNIQUE NOT NULL,

    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    started_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(50) NOT NULL,  -- RUNNING/SUCCESS/FAILED

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_pipeline_run_domain_entity
    ON pipeline_run(domain_code, entity_id);

CREATE INDEX IF NOT EXISTS idx_pipeline_run_status
    ON pipeline_run(status);


-- ------------------------
-- PIPELINE STAGE RUN
-- ------------------------
CREATE TABLE IF NOT EXISTS pipeline_stage_run (
                                                  id BIGSERIAL PRIMARY KEY,

                                                  run_id VARCHAR(255) NOT NULL,
    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    stage VARCHAR(50) NOT NULL,    -- INGEST/TRANSFORM/ENRICH
    status VARCHAR(50) NOT NULL,   -- RUNNING/SUCCESS/FAILED

    started_at TIMESTAMP WITHOUT TIME ZONE,
    finished_at TIMESTAMP WITHOUT TIME ZONE
    );

CREATE INDEX IF NOT EXISTS idx_stage_run_run_id ON pipeline_stage_run(run_id);
CREATE INDEX IF NOT EXISTS idx_stage_run_stage ON pipeline_stage_run(stage);


-- ------------------------
-- JOB EXECUTION
-- ------------------------
CREATE TABLE IF NOT EXISTS job_execution (
                                             id BIGSERIAL PRIMARY KEY,

                                             run_id VARCHAR(255) NOT NULL,
    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    stage VARCHAR(50) NOT NULL,
    job_platform VARCHAR(50),       -- spark/databricks/snowflake
    job_identifier VARCHAR(255),

    started_at TIMESTAMP WITHOUT TIME ZONE,
    finished_at TIMESTAMP WITHOUT TIME ZONE,

    status VARCHAR(50) NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_job_execution_run_id ON job_execution(run_id);
CREATE INDEX IF NOT EXISTS idx_job_execution_stage ON job_execution(stage);


-- ------------------------
-- PIPELINE EVENT LOG
-- ------------------------
CREATE TABLE IF NOT EXISTS pipeline_event_log (
                                                  id BIGSERIAL PRIMARY KEY,

                                                  run_id VARCHAR(255) NOT NULL,
    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    stage VARCHAR(50),
    status VARCHAR(50),

    timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    job_identifier VARCHAR(255),
    error_details TEXT
    );

CREATE INDEX IF NOT EXISTS idx_event_log_run_id ON pipeline_event_log(run_id);


-- ------------------------
-- SQL EXECUTION LOG
-- ------------------------
CREATE TABLE IF NOT EXISTS sql_execution_log (
                                                 id BIGSERIAL PRIMARY KEY,

                                                 run_id VARCHAR(255) NOT NULL,
    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    sql_path TEXT,
    status VARCHAR(50),

    timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    result TEXT
    );

CREATE INDEX IF NOT EXISTS idx_sql_log_run_id ON sql_execution_log(run_id);


-- ------------------------
-- OUTBOX
-- ------------------------
CREATE TABLE IF NOT EXISTS job_completion_outbox (
                                                     id BIGSERIAL PRIMARY KEY,

                                                     run_id VARCHAR(255) NOT NULL,
    domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    stage VARCHAR(50),
    terminal_state VARCHAR(50),

    event_payload TEXT,

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_outbox_run_id ON job_completion_outbox(run_id);



-- ==========================================================
-- INSERTS (RUNTIME)
-- ==========================================================

-- Main pipeline run
INSERT INTO pipeline_run (run_id, domain_code, entity_id, started_at, completed_at, status)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', NOW() - INTERVAL '10 min', NOW(), 'SUCCESS')
    ON CONFLICT (run_id) DO NOTHING;


-- Stage runs
INSERT INTO pipeline_stage_run (run_id, domain_code, entity_id, stage, status, started_at, finished_at)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'INGEST',     'SUCCESS', NOW() - INTERVAL '10 min', NOW() - INTERVAL '8 min'),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'TRANSFORM',  'SUCCESS', NOW() - INTERVAL '8 min',  NOW() - INTERVAL '3 min'),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'ENRICH',     'SUCCESS', NOW() - INTERVAL '3 min',  NOW())
;


-- Job executions
INSERT INTO job_execution (run_id, domain_code, entity_id, stage, job_platform, job_identifier, started_at, finished_at, status)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'INGEST',    'spark',      'spark_app_001', NOW() - INTERVAL '10 min', NOW() - INTERVAL '8 min', 'SUCCESS'),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'TRANSFORM', 'databricks', 'db_job_90211',  NOW() - INTERVAL '8 min',  NOW() - INTERVAL '3 min', 'SUCCESS'),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'ENRICH',    'spark',      'spark_app_002', NOW() - INTERVAL '3 min',  NOW(), 'SUCCESS')
;


-- Pipeline event logs
INSERT INTO pipeline_event_log (run_id, domain_code, entity_id, stage, status, job_identifier, error_details)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'INGEST',    'STARTED', 'spark_app_001', NULL),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'INGEST',    'SUCCESS', 'spark_app_001', NULL),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'TRANSFORM', 'SUCCESS', 'db_job_90211',  NULL),
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'ENRICH',    'SUCCESS', 'spark_app_002', NULL)
;


-- SQL execution logs (for enrichment step)
INSERT INTO sql_execution_log (run_id, domain_code, entity_id, sql_path, status, result)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header',
     's3://mdhp-sql/claims/gold/claim_gold.sql',
     'SUCCESS',
     'Rows inserted: 102345')
;


-- Outbox event for downstream notifications
INSERT INTO job_completion_outbox (run_id, domain_code, entity_id, stage, terminal_state, event_payload)
VALUES
    ('RUN-20260130-0001', 'claims', 'clm_claim_header', 'PIPELINE', 'SUCCESS',
     '{"runId":"RUN-20260130-0001","domain":"claims","entity":"clm_claim_header","status":"SUCCESS"}')
;
