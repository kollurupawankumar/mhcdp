CREATE TABLE IF NOT EXISTS metadata_release (
                                                release_id VARCHAR(80) PRIMARY KEY,
    env VARCHAR(20) NOT NULL,
    git_commit VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT now()
    );


CREATE TABLE IF NOT EXISTS metadata_release_item (
                                                     release_id VARCHAR(80) NOT NULL,
    object_type VARCHAR(50) NOT NULL,
    object_name VARCHAR(200) NOT NULL,
    version INT NOT NULL,
    PRIMARY KEY (release_id, object_type, object_name, version)
    );


CREATE TABLE IF NOT EXISTS metadata_object_store (
                                                     object_type VARCHAR(50) NOT NULL,
    object_name VARCHAR(200) NOT NULL,
    version INT NOT NULL,
    domain VARCHAR(100),
    payload_json JSONB NOT NULL,
    checksum VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL,
    release_id VARCHAR(80) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    PRIMARY KEY (object_type, object_name, version)
    );


CREATE INDEX IF NOT EXISTS idx_mos_active ON metadata_object_store(object_type, object_name, status);