ALTER TABLE scan_instance ALTER COLUMN exposure_window_id TYPE BIGINT;

ALTER TABLE    exposure_windows_at_test_registration ALTER COLUMN id TYPE BIGINT, 
                                                     ALTER COLUMN exposure_window_test_result_id TYPE BIGINT;
ALTER SEQUENCE exposure_windows_at_test_registration_id_seq AS BIGINT;

ALTER TABLE scan_instances_at_test_registration ALTER COLUMN exposure_window_id TYPE BIGINT;

ALTER TABLE    exposure_window_test_result ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE exposure_window_test_result_id_seq AS BIGINT;

ALTER TABLE    summarized_exposure_windows_with_user_metadata ALTER COLUMN id TYPE BIGINT;
ALTER SEQUENCE summarized_exposure_windows_with_user_metadata_id_seq AS BIGINT;
