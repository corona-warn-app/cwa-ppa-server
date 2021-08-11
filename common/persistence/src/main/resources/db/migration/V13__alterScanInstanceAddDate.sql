ALTER TABLE scan_instance DROP CONSTRAINT fk_exposure_window_id;
ALTER TABLE scan_instance ADD COLUMN submitted_at DATE;
