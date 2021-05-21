
CREATE OR REPLACE FUNCTION data_donation.random_bool()
	RETURNS boolean
	LANGUAGE plpgsql
	AS $$
	BEGIN
		RETURN random() > 0.5;
	END;
	$$ ;

CREATE OR REPLACE FUNCTION data_donation.random_int()
	RETURNS integer
	LANGUAGE plpgsql
	AS $$
	BEGIN
		RETURN floor(random() * 10 + 1)::integer;
	END;
	$$;

CREATE OR REPLACE FUNCTION data_donation.random_string(num_characters integer)
	RETURNS TEXT
	LANGUAGE plpgsql
	AS $$
	BEGIN
		RETURN left(concat(md5(random()::text), md5(random()::text), md5(random()::text), md5(random()::text)), num_characters);
	END;
	$$;

CREATE OR REPLACE FUNCTION data_donation.generate_test_data(num integer)
	RETURNS VOID
	LANGUAGE plpgsql
	AS $$
	BEGIN

	SET search_path TO data_donation;

	INSERT INTO exposure_risk_metadata(
		risk_level,
		risk_level_changed,
		most_recent_date_changed,
		federal_state,
		administrative_unit,
		age_group,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_int(),
		random_bool(),
		random_bool(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO exposure_window(
		report_type,
		infectiousness,
		callibration_confidence,
		transmission_risk_level,
		normalized_time,
		cwa_version_major,
		cwa_version_minor,
		cwa_version_patch,
		app_config_etag,
		ios_version_major,
		ios_version_minor,
		ios_version_patch,
		android_api_level,
		android_enf_version,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random(),
		random_int(),
		random_int(),
		random_int(),
		random_string(100),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO scan_instance(
		exposure_window_id,
		typical_attenuation,
		minimum_attenuation,
		seconds_since_last_scan
	)
	SELECT
		random_int(),
		random_int(),
		random_int(),
		random_int()
	FROM generate_series(1, num);

	INSERT INTO test_result_metadata(
		test_result,
		hours_since_test_registration,
		risk_level_at_test_registration,
		days_since_most_recent_date_at_risk_level_at_test_registration,
		hours_since_high_risk_warning_at_test_registration,
		federal_state,
		administrative_unit,
		age_group,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO key_submission_metadata_with_user_metadata(
		submitted,
		submitted_after_symptom_flow,
		submitted_with_teletan,
		hours_since_reception_of_test_result,
		hours_since_test_registration,
		days_since_most_recent_date_at_risk_level_at_test_registration,
		hours_since_high_risk_warning_at_test_registration,
		federal_state,
		administrative_unit,
		age_group,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_bool(),
		random_bool(),
		random_bool(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO key_submission_metadata_with_client_metadata(
		submitted,
		submitted_in_background,
		submitted_after_cancel,
		submitted_after_symptom_flow,
		advanced_consent_given,
		last_submission_flow_screen,
		cwa_version_major,
		cwa_version_minor,
		cwa_version_patch,
		app_config_etag,
		ios_version_major,
		ios_version_minor,
		ios_version_patch,
		android_api_level,
		android_enf_version,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_string(100),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO user_metadata(
		federal_state,
		administrative_unit,
		age_group,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

	INSERT INTO client_metadata(
		cwa_version_major,
		cwa_version_minor,
		cwa_version_patch,
		app_config_etag,
		ios_version_major,
		ios_version_minor,
		ios_version_patch,
		android_api_level,
		android_enf_version,
		android_ppac_basic_integrity,
		android_ppac_cts_profile_match,
		android_ppac_evaluation_type_basic,
		android_ppac_evaluation_type_hardware_backed
	)
	SELECT
		random_int(),
		random_int(),
		random_int(),
		random_string(100),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_int(),
		random_bool(),
		random_bool(),
		random_bool(),
		random_bool()
	FROM generate_series(1, num);

END;
$$
