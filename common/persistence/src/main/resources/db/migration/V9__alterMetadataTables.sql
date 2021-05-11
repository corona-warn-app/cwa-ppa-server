ALTER TABLE key_submission_metadata_with_client_metadata
  ADD submitted_after_rapid_antigen_test BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE key_submission_metadata_with_user_metadata
  ADD submitted_after_rapid_antigen_test BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE exposure_risk_metadata
  ADD pt_risk_level INTEGER NOT NULL,
  ADD pt_risk_level_changed BOOLEAN NOT NULL,
  ADD pt_most_recent_date_at_risk_level DATE NOT NULL DEFAULT CURRENT_DATE,
  ADD pt_most_recent_date_changed BOOLEAN NOT NULL;

