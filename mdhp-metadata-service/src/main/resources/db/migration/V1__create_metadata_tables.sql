-- Source metadata: ingestion configs
CREATE TABLE IF NOT EXISTS source_metadata (
  id BIGSERIAL PRIMARY KEY,
  entity_id VARCHAR(255),
  subject_area VARCHAR(255),
  entity_name VARCHAR(255),
  source_type VARCHAR(50),
  source_config JSONB,
  version VARCHAR(50),
  active_flag BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Transformation metadata: raw -> silver
CREATE TABLE IF NOT EXISTS transformation_metadata (
  id BIGSERIAL PRIMARY KEY,
  entity_id VARCHAR(255),
  input_table VARCHAR(255),
  output_table VARCHAR(255),
  transform_config JSONB,
  version INTEGER,
  active_flag BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Enrichment metadata: silver -> gold
CREATE TABLE IF NOT EXISTS enrichment_metadata (
  id BIGSERIAL PRIMARY KEY,
  entity_id VARCHAR(255),
  enrichment_mode VARCHAR(50),
  sql_path VARCHAR(512),
  sql_files TEXT,
  enrich_config JSONB,
  version INTEGER,
  active_flag BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
