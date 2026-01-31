-- ==========================================
-- 01_metadata.sql
-- METADATA TABLES (CREATE + INSERT)
-- ==========================================

-- ------------------------
-- DOMAIN MASTER
-- ------------------------
CREATE TABLE IF NOT EXISTS domain_master (
                                             id BIGSERIAL PRIMARY KEY,
                                             domain_code VARCHAR(100) UNIQUE NOT NULL,
    domain_name VARCHAR(255) NOT NULL,
    run_frequency VARCHAR(50) NOT NULL,           -- hourly/daily/weekly/monthly
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
    );

-- ------------------------
-- ENTITY MASTER
-- ------------------------
CREATE TABLE IF NOT EXISTS entity_master (
                                             id BIGSERIAL PRIMARY KEY,
                                             domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) UNIQUE NOT NULL,
    subject_area VARCHAR(255),
    entity_name VARCHAR(255) NOT NULL,
    entity_description TEXT,
    entity_frequency VARCHAR(50),                 -- optional override
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_entity_domain_code ON entity_master(domain_code);
CREATE INDEX IF NOT EXISTS idx_entity_active ON entity_master(active_flag);

-- ------------------------
-- SOURCE METADATA
-- ------------------------
CREATE TABLE IF NOT EXISTS source_metadata (
                                               id BIGSERIAL PRIMARY KEY,
                                               domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_config JSONB NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE(entity_id, version)
    );

CREATE INDEX IF NOT EXISTS idx_source_entity ON source_metadata(entity_id);
CREATE INDEX IF NOT EXISTS idx_source_domain ON source_metadata(domain_code);
CREATE INDEX IF NOT EXISTS idx_source_active ON source_metadata(active_flag);

-- ------------------------
-- TRANSFORMATION METADATA
-- ------------------------
CREATE TABLE IF NOT EXISTS transformation_metadata (
                                                       id BIGSERIAL PRIMARY KEY,
                                                       domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    input_table VARCHAR(255),
    output_table VARCHAR(255),
    transform_config JSONB NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE(entity_id, version)
    );

CREATE INDEX IF NOT EXISTS idx_trans_entity ON transformation_metadata(entity_id);
CREATE INDEX IF NOT EXISTS idx_trans_domain ON transformation_metadata(domain_code);
CREATE INDEX IF NOT EXISTS idx_trans_active ON transformation_metadata(active_flag);

-- ------------------------
-- ENRICHMENT METADATA
-- ------------------------
CREATE TABLE IF NOT EXISTS enrichment_metadata (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   domain_code VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    enrichment_mode VARCHAR(50),
    sql_path VARCHAR(512),
    sql_files TEXT,
    enrich_config JSONB NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE(entity_id, version)
    );

CREATE INDEX IF NOT EXISTS idx_enrich_entity ON enrichment_metadata(entity_id);
CREATE INDEX IF NOT EXISTS idx_enrich_domain ON enrichment_metadata(domain_code);
CREATE INDEX IF NOT EXISTS idx_enrich_active ON enrichment_metadata(active_flag);



-- ==========================================================
-- INSERTS (METADATA)
-- ==========================================================

-- --------------------
-- DOMAINS
-- --------------------
INSERT INTO domain_master (domain_code, domain_name, run_frequency, active_flag)
VALUES
    ('workforce', 'Workforce', 'daily', TRUE),
    ('finance',   'Finance',   'hourly', TRUE),
    ('claims',    'Claims',    'hourly', TRUE)
    ON CONFLICT (domain_code) DO NOTHING;


-- --------------------
-- ENTITIES
-- --------------------
INSERT INTO entity_master
(domain_code, entity_id, subject_area, entity_name, entity_description, entity_frequency, active_flag)
VALUES
    ('workforce', 'wf_employee_master', 'workforce', 'employee_master',
     'Master employee dataset (HRMS)', NULL, TRUE),

    ('finance', 'fin_gl_transactions', 'finance', 'gl_transactions',
     'General ledger transactions', 'hourly', TRUE),

    ('claims', 'clm_claim_header', 'claims', 'claim_header',
     'Claims header table from claims system', 'hourly', TRUE),

    ('claims', 'clm_claim_payments', 'claims', 'claim_payments',
     'Claims payments table from payment module', 'daily', TRUE)
    ON CONFLICT (entity_id) DO NOTHING;


-- ==========================================================
-- SOURCE METADATA INSERTS
-- ==========================================================

-- Workforce - Employee Master (JDBC)
INSERT INTO source_metadata
(domain_code, entity_id, source_type, source_config, version, active_flag)
VALUES
    ('workforce', 'wf_employee_master', 'DB',
     '{
       "connection": {
         "secretRef": "kv/workforce/hrms-db",
         "driver": "org.postgresql.Driver"
       },
       "extract": {
         "mode": "incremental",
         "table": "public.employee_master",
         "watermarkColumn": "updated_at",
         "watermarkType": "timestamp",
         "fetchSize": 5000
       },
       "target": {
         "rawPath": "s3://mdhp-raw/workforce/employee_master/",
         "fileFormat": "parquet",
         "partitionBy": ["ingest_date"]
       }
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;


-- Finance - GL Transactions (Kafka)
INSERT INTO source_metadata
(domain_code, entity_id, source_type, source_config, version, active_flag)
VALUES
    ('finance', 'fin_gl_transactions', 'kafka',
     '{
       "kafka": {
         "bootstrapServers": "kafka01:9092,kafka02:9092",
         "topic": "finance.gl.transactions",
         "consumerGroup": "mdhp-finance-gl",
         "startingOffsets": "latest"
       },
       "target": {
         "rawPath": "s3://mdhp-raw/finance/gl_transactions/",
         "fileFormat": "delta",
         "checkpointPath": "s3://mdhp-checkpoints/finance/gl_transactions/"
       }
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;


-- Claims - Claim Header (JDBC)
INSERT INTO source_metadata
(domain_code, entity_id, source_type, source_config, version, active_flag)
VALUES
    ('claims', 'clm_claim_header', 'DB',
     '{
       "connection": {
         "secretRef": "kv/claims/claims-db",
         "driver": "org.postgresql.Driver"
       },
       "extract": {
         "mode": "incremental",
         "table": "public.claim_header",
         "watermarkColumn": "updated_at",
         "watermarkType": "timestamp",
         "fetchSize": 10000
       },
       "target": {
         "rawPath": "s3://mdhp-raw/claims/claim_header/",
         "fileFormat": "parquet",
         "partitionBy": ["ingest_date"]
       }
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;



-- ==========================================================
-- TRANSFORMATION METADATA INSERTS
-- ==========================================================

-- Claim Header raw->silver
INSERT INTO transformation_metadata
(domain_code, entity_id, input_table, output_table, transform_config, version, active_flag)
VALUES
    ('claims', 'clm_claim_header',
     'raw_claims.claim_header',
     'silver_claims.claim_header',
     '{
       "type": "merge",
       "mergeKeys": ["claim_id"],
       "deduplicate": true,
       "qualityChecks": {
         "notNull": ["claim_id", "policy_id"],
         "acceptedValues": {
           "claim_status": ["OPEN", "CLOSED", "REJECTED"]
         }
       },
       "partitionBy": ["ingest_date"]
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;


-- Workforce employee master raw->silver
INSERT INTO transformation_metadata
(domain_code, entity_id, input_table, output_table, transform_config, version, active_flag)
VALUES
    ('workforce', 'wf_employee_master',
     'raw_workforce.employee_master',
     'silver_workforce.employee_master',
     '{
       "type": "append",
       "deduplicate": true,
       "mergeKeys": ["employee_id"],
       "standardizeColumns": true
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;



-- ==========================================================
-- ENRICHMENT METADATA INSERTS
-- ==========================================================

-- Claims claim_header silver->gold
INSERT INTO enrichment_metadata
(domain_code, entity_id, enrichment_mode, sql_path, sql_files, enrich_config, version, active_flag)
VALUES
    ('claims', 'clm_claim_header',
     'sql',
     's3://mdhp-sql/claims/gold/',
     'claim_gold.sql',
     '{
       "goldTable": "gold_claims.claim_summary",
       "writeMode": "overwrite",
       "dependencies": ["silver_claims.claim_header"]
     }'::jsonb,
     1, TRUE)
    ON CONFLICT (entity_id, version) DO NOTHING;
