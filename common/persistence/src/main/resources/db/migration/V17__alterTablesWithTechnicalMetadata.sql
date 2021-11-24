ALTER TABLE exposure_windows_at_test_registration
  ADD submitted_at DATE NOT NULL DEFAULT CURRENT_DATE,
  ADD android_ppac_basic_integrity BOOLEAN,
  ADD android_ppac_cts_profile_match BOOLEAN,
  ADD android_ppac_evaluation_type_basic BOOLEAN,
  ADD android_ppac_evaluation_type_hardware_backed BOOLEAN;

ALTER TABLE scan_instances_at_test_registration
  ADD android_ppac_basic_integrity BOOLEAN,
  ADD android_ppac_cts_profile_match BOOLEAN,
  ADD android_ppac_evaluation_type_basic BOOLEAN,
  ADD android_ppac_evaluation_type_hardware_backed BOOLEAN;

ALTER TABLE scan_instance
  ADD android_ppac_basic_integrity BOOLEAN,
  ADD android_ppac_cts_profile_match BOOLEAN,
  ADD android_ppac_evaluation_type_basic BOOLEAN,
  ADD android_ppac_evaluation_type_hardware_backed BOOLEAN;
