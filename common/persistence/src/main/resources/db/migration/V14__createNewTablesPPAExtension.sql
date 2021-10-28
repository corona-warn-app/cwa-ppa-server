CREATE TABLE exposure_window_test_result (
    id SERIAL PRIMARY KEY,
    test_result SMALLINT NOT NULL,
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
    android_ppac_evaluation_type_hardware_backed BOOLEAN
);

CREATE TABLE exposure_windows_at_test_registration (
    id SERIAL PRIMARY KEY,
    exposure_window_test_result_id INTEGER NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    report_type SMALLINT NOT NULL,
    infectiousness SMALLINT NOT NULL,
    calibration_confidence SMALLINT NOT NULL,
    transmission_risk_level SMALLINT NOT NULL,
    normalized_time numeric NOT NULL,
    after_test_registration BOOLEAN
);

CREATE TABLE scan_instances_at_test_registration (
    id SERIAL PRIMARY KEY,
    exposure_window_id INTEGER NOT NULL,
    typical_attenuation SMALLINT NOT NULL,
    minimum_attenuation SMALLINT NOT NULL,
    seconds_since_last_scan SMALLINT NOT NULL,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE summarized_exposure_windows_with_user_metadata (
    id SERIAL PRIMARY KEY,
    batch_id VARCHAR NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    transmission_risk_level SMALLINT NOT NULL,
    normalized_time numeric NOT NULL,
    federal_state INTEGER NOT NULL,
    administrative_unit INTEGER NOT NULL,
    age_group INTEGER NOT NULL,
    submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
    android_ppac_basic_integrity BOOLEAN,
    android_ppac_cts_profile_match BOOLEAN,
    android_ppac_evaluation_type_basic BOOLEAN,
    android_ppac_evaluation_type_hardware_backed BOOLEAN
);

ALTER TABLE exposure_risk_metadata
  ADD cwa_version_major INTEGER NOT NULL,
  ADD cwa_version_minor INTEGER NOT NULL,
  ADD cwa_version_patch INTEGER NOT NULL;

ALTER TABLE test_result_metadata
  ADD cwa_version_major INTEGER NOT NULL,
  ADD cwa_version_minor INTEGER NOT NULL,
  ADD cwa_version_patch INTEGER NOT NULL;

ALTER TABLE key_submission_metadata_with_user_metadata
  ADD cwa_version_major INTEGER NOT NULL,
  ADD cwa_version_minor INTEGER NOT NULL,
  ADD cwa_version_patch INTEGER NOT NULL;

ALTER TABLE test_result_metadata
    add exposureWindowsUntilTestResult;
