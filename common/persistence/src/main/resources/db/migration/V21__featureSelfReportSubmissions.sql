CREATE TABLE IF NOT EXISTS srs_one_time_password (
    password VARCHAR(36) PRIMARY KEY,
    redemption_timestamp BIGINT,
    expiration_timestamp BIGINT NOT NULL,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN
);

CREATE TABLE IF NOT EXISTS android_id (
    id CHAR(44) PRIMARY KEY,
    expiration_date BIGINT NOT NULL,
    last_used_srs BIGINT
);

ALTER TABLE api_token ADD COLUMN IF NOT EXISTS last_used_srs BIGINT;
