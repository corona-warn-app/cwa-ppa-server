ALTER TABLE key_submission_metadata_with_client_metadata
  ADD submitted_with_check_ins BOOLEAN DEFAULT NULL;
ALTER TABLE key_submission_metadata_with_client_metadata ALTER COLUMN submitted_with_check_ins DROP DEFAULT;
ALTER TABLE key_submission_metadata_with_client_metadata ALTER COLUMN submitted_with_check_ins SET DEFAULT FALSE;

ALTER TABLE key_submission_metadata_with_user_metadata
  ADD submitted_after_rapid_antigen_test BOOLEAN NOT NULL DEFAULT FALSE,
  ADD pt_days_since_most_recent_date_at_risk_level INTEGER,
  ADD pt_hours_since_high_risk_warning INTEGER;

ALTER TABLE exposure_risk_metadata
  ADD pt_risk_level INTEGER,
  ADD pt_risk_level_changed BOOLEAN,
  ADD pt_most_recent_date_at_risk_level DATE DEFAULT NULL,
  ADD pt_most_recent_date_changed BOOLEAN;
ALTER TABLE exposure_risk_metadata ALTER COLUMN pt_most_recent_date_at_risk_level DROP DEFAULT;
ALTER TABLE exposure_risk_metadata ALTER COLUMN pt_most_recent_date_at_risk_level SET DEFAULT CURRENT_DATE;

ALTER TABLE test_result_metadata
  ADD pt_risk_level INTEGER,
  ADD pt_days_since_most_recent_date_at_risk_level INTEGER,
  ADD pt_hours_since_high_risk_warning INTEGER;
