DELETE FROM scan_instance
    WHERE NOT EXISTS (
        SELECT 1 FROM exposure_window WHERE exposure_window_id=exposure_window.id
    );

ALTER TABLE scan_instance
  ADD CONSTRAINT fk_exposure_window_id FOREIGN KEY (exposure_window_id)
  REFERENCES exposure_window(id)
  ON DELETE CASCADE;
