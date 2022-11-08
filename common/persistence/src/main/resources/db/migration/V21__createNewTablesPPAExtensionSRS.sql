CREATE TABLE srs_one_time_password (
    password VARCHAR(36) PRIMARY KEY,
    redemption_timestamp BIGINT,
    expiration_timestamp BIGINT NOT NULL,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

CREATE TABLE android_id (
    id SERIAL PRIMARY KEY,
    expiration_date DATE NOT NULL,
    last_used_for_srs DATE
);