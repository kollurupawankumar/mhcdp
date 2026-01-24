-- Runtime state tables for MDHP (Phase 6 wired)
CREATE TABLE IF NOT EXISTS pipeline_run (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255) UNIQUE,
  started_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  completed_at TIMESTAMP WITHOUT TIME ZONE,
  status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS pipeline_stage_run (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255),
  stage VARCHAR(50),
  status VARCHAR(50),
  started_at TIMESTAMP WITHOUT TIME ZONE,
  finished_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS job_execution (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255),
  stage VARCHAR(50),
  job_platform VARCHAR(50),
  job_identifier VARCHAR(255),
  started_at TIMESTAMP WITHOUT TIME ZONE,
  finished_at TIMESTAMP WITHOUT TIME ZONE,
  status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS pipeline_event_log (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255),
  stage VARCHAR(50),
  status VARCHAR(50),
  timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  job_identifier VARCHAR(255),
  error_details TEXT
);

CREATE TABLE IF NOT EXISTS sql_execution_log (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255),
  sql_path TEXT,
  status VARCHAR(50),
  timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  result TEXT
);

CREATE TABLE IF NOT EXISTS job_completion_outbox (
  id BIGSERIAL PRIMARY KEY,
  run_id VARCHAR(255),
  stage VARCHAR(50),
  terminal_state VARCHAR(50),
  event_payload TEXT,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
