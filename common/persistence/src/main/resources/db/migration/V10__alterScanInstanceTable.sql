ALTER TABLE scan_instance
  ADD CONSTRAINT fk_exposure_window_id FOREIGN KEY (exposure_window_id)
  REFERENCES exposure_window(id)
  ON DELETE CASCADE;
