ALTER SEQUENCE exposure_risk_metadata_id_seq AS bigint;
ALTER TABLE exposure_risk_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE exposure_window_id_seq AS bigint;
ALTER TABLE exposure_window ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE test_result_metadata_id_seq AS bigint;
ALTER TABLE test_result_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE key_submission_metadata_with_user_metadata_id_seq AS bigint;
ALTER TABLE key_submission_metadata_with_user_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE key_submission_metadata_with_client_metadata_id_seq AS bigint;
ALTER TABLE key_submission_metadata_with_client_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE user_metadata_id_seq AS bigint;
ALTER TABLE user_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE client_metadata_id_seq AS bigint;
ALTER TABLE client_metadata ALTER COLUMN id TYPE bigint;

ALTER SEQUENCE device_token_id_seq AS bigint;
ALTER TABLE device_token ALTER COLUMN id TYPE bigint;
