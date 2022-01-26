ALTER TABLE scan_instance
  ALTER COLUMN exposure_window_id TYPE bigint;

ALTER TABLE exposure_windows_at_test_registration
  ALTER COLUMN exposure_window_test_result_id TYPE bigint;

ALTER TABLE scan_instances_at_test_registration
  ALTER COLUMN exposure_window_id TYPE bigint;
