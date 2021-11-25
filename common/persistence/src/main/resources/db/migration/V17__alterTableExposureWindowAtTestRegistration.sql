ALTER TABLE exposure_windows_at_test_registration
	ADD COLUMN submitted_at DATE NOT NULL DEFAULT CURRENT_DATE;
