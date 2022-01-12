ALTER TABLE scan_instances_at_test_registration
  ALTER COLUMN seconds_since_last_scan TYPE INTEGER,
  ALTER COLUMN typical_attenuation TYPE INTEGER,
  ALTER COLUMN minimum_attenuation TYPE INTEGER;

ALTER SEQUENCE scan_instances_at_test_registration_id_seq AS bigint;

ALTER TABLE scan_instances_at_test_registration ALTER COLUMN id TYPE bigint;

