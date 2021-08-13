DROP VIEW IF EXISTS "scan_instance_view";

CREATE OR REPLACE VIEW scan_instance_view AS
	SELECT	exposure_window_id,
			scan_instance.id AS scan_instance_id,
			typical_attenuation,
			minimum_attenuation,
			seconds_since_last_scan,
			scan_instance.submitted_at
	FROM data_donation.scan_instance
		INNER JOIN data_donation.exposure_window ON exposure_window_id = exposure_window.id;
