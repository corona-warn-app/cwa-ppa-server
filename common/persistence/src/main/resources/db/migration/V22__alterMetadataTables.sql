ALTER TABLE key_submission_metadata_with_user_metadata
  ADD submission_type INTEGER;

ALTER TABLE key_submission_metadata_with_client_metadata
  ADD submission_type INTEGER;

ALTER TABLE exposure_risk_metadata
  ADD submission_type INTEGER;