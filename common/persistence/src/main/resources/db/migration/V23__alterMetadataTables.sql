ALTER TABLE key_submission_metadata_with_user_metadata ADD COLUMN IF NOT EXISTS submission_type SMALLINT;

ALTER TABLE key_submission_metadata_with_client_metadata ADD COLUMN IF NOT EXISTS submission_type SMALLINT;
