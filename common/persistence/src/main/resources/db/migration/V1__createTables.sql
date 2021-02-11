-- Metrics tables

CREATE TABLE exposure_risk_metadata (
    id SERIAL PRIMARY KEY,
    risk_level INTEGER NOT NULL,
    risk_level_changed BOOLEAN NOT NULL,
    most_recent_date_at_risk_level DATE NOT NULL DEFAULT CURRENT_DATE,
    most_recent_date_changed BOOLEAN NOT NULL,
    federal_state INTEGER NOT NULL,
    administrative_unit INTEGER NOT NULL,
    age_group INTEGER NOT NULL,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

CREATE TABLE exposure_window (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    report_type INTEGER NOT NULL,
    infectiousness INTEGER NOT NULL,
    callibration_confidence INTEGER NOT NULL,
    transmission_risk_level INTEGER NOT NULL,
    normalized_time numeric NOT NULL,
    cwa_version_major INTEGER NOT NULL,
    cwa_version_minor INTEGER NOT NULL,
    cwa_version_patch INTEGER NOT NULL,
    app_config_etag VARCHAR(100) NOT NULL,
    ios_version_major INTEGER,
    ios_version_minor INTEGER,
    ios_version_patch INTEGER,
    android_api_level INTEGER,
    android_enf_version INTEGER,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

CREATE TABLE scan_instance (
    id SERIAL PRIMARY KEY,
    exposure_window_id INTEGER NOT NULL,
    typical_attenuation INTEGER NOT NULL,
    minimum_attenuation INTEGER NOT NULL,
    seconds_since_last_scan INTEGER NOT NULL
);

CREATE TABLE test_result_metadata (
    id SERIAL PRIMARY KEY,
    test_result INTEGER NOT NULL,
    hours_since_test_registration INTEGER NOT NULL,
    risk_level_at_test_registration INTEGER NOT NULL,
    days_since_most_recent_date_at_risk_level_at_test_registration INTEGER NOT NULL,
    hours_since_high_risk_warning_at_test_registration INTEGER NOT NULL,
    federal_state INTEGER NOT NULL,
    administrative_unit INTEGER NOT NULL,
    age_group INTEGER NOT NULL,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

CREATE TABLE key_submission_metadata_with_user_metadata (
    id SERIAL PRIMARY KEY,
    submitted BOOLEAN NOT NULL,
    submitted_after_symptom_flow BOOLEAN NOT NULL,
    submitted_with_teletan BOOLEAN NOT NULL,
    hours_since_reception_of_test_result INTEGER NOT NULL,
    hours_since_test_registration INTEGER NOT NULL,
    days_since_most_recent_date_at_risk_level_at_test_registration INTEGER NOT NULL,
    hours_since_high_risk_warning_at_test_registration INTEGER NOT NULL,
    federal_state INTEGER NOT NULL,
    administrative_unit INTEGER NOT NULL,
    age_group INTEGER NOT NULL,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

CREATE TABLE key_submission_metadata_with_client_metadata (
    id SERIAL PRIMARY KEY,
    submitted BOOLEAN NOT NULL,
    submitted_in_background BOOLEAN NOT NULL,
    submitted_after_cancel BOOLEAN NOT NULL,
    submitted_after_symptom_flow BOOLEAN NOT NULL,
    advanced_consent_given BOOLEAN NOT NULL,
    last_submission_flow_screen INTEGER NOT NULL,
    cwa_version_major INTEGER NOT NULL,
    cwa_version_minor INTEGER NOT NULL,
    cwa_version_patch INTEGER NOT NULL,
    app_config_etag VARCHAR(100) NOT NULL,
    ios_version_major INTEGER,
    ios_version_minor INTEGER,
    ios_version_patch INTEGER,
    android_api_level INTEGER,
    android_enf_version INTEGER,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

-- EDUS tables

CREATE TABLE one_time_password (
    password VARCHAR(36) PRIMARY KEY,
    redemption_timestamp BIGINT,
    expiration_timestamp BIGINT NOT NULL,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN,
    android_ppac_advice BOOLEAN
);

-- PPAC tables

CREATE TABLE api_token (
    api_token VARCHAR PRIMARY KEY,
    expiration_date BIGINT NOT NULL,
    created_at BIGINT NOT NULL,
    last_used_edus BIGINT,
    last_used_ppac BIGINT
);

CREATE TABLE device_token (
  id SERIAL PRIMARY KEY,
  device_token_hash  BYTEA UNIQUE NOT NULL,
  created_at BIGINT NOT NULL
);

CREATE TABLE salt (
    salt VARCHAR(32) PRIMARY KEY,
    created_at bigint NOT NULL
);

-- AC
GRANT SELECT, INSERT, UPDATE ON TABLE
  data_donation.api_token,
  data_donation.device_token,
  data_donation.exposure_risk_metadata,
  data_donation.exposure_window,
  data_donation.key_submission_metadata_with_client_metadata,
  data_donation.key_submission_metadata_with_user_metadata,
  data_donation.one_time_password,
  data_donation.salt,
  data_donation.scan_instance,
  data_donation.test_result_metadata
  TO cwa_ppdd_ppac;

GRANT ALL ON ALL SEQUENCES IN SCHEMA data_donation TO cwa_ppdd_ppac;

GRANT SELECT, UPDATE ON TABLE
  data_donation.one_time_password
  TO cwa_ppdd_edus;

