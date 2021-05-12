ALTER TABLE key_submission_metadata_with_client_metadata
  ADD submitted_after_rapid_antigen_test BOOLEAN NOT NULL DEFAULT FALSE,
  ADD submitted_with_check_ins BOOLEAN DEFAULT NULL;


ALTER TABLE key_submission_metadata_with_user_metadata
  ADD submitted_after_rapid_antigen_test BOOLEAN NOT NULL DEFAULT FALSE,
  ADD pt_days_since_most_recent_date_at_risk_level INTEGER,
  ADD pt_hours_since_high_risk_warning INTEGER;

ALTER TABLE exposure_risk_metadata
  ADD pt_risk_level INTEGER,
  ADD pt_risk_level_changed BOOLEAN,
  ADD pt_most_recent_date_at_risk_level DATE DEFAULT CURRENT_DATE,
  ADD pt_most_recent_date_changed BOOLEAN;

ALTER TABLE test_result_metadata
  ADD pt_risk_level INTEGER,
  ADD pt_days_since_most_recent_date_at_risk_level INTEGER,
  ADD pt_hours_since_high_risk_warning INTEGER;
