ALTER TABLE exposure_window
	ALTER COLUMN android_api_level   TYPE BIGINT,
	ALTER COLUMN android_enf_version TYPE BIGINT;

ALTER TABLE key_submission_metadata_with_client_metadata
	ALTER COLUMN android_api_level   TYPE BIGINT,
	ALTER COLUMN android_enf_version TYPE BIGINT;

ALTER TABLE client_metadata
	ALTER COLUMN android_api_level   TYPE BIGINT,
	ALTER COLUMN android_enf_version TYPE BIGINT;
