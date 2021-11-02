GRANT SELECT, INSERT, UPDATE ON TABLE
    data_donation.exposure_window_test_result,
    data_donation.exposure_windows_at_test_registration,
    data_donation.scan_instances_at_test_registration,
    data_donation.summarized_exposure_windows_with_user_metadata
    TO cwa_ppdd_ppac;

GRANT ALL ON ALL SEQUENCES IN SCHEMA data_donation TO cwa_ppdd_ppac;

GRANT SELECT, DELETE ON TABLE
    data_donation.exposure_window_test_result,
    data_donation.exposure_windows_at_test_registration,
    data_donation.scan_instances_at_test_registration,
    data_donation.summarized_exposure_windows_with_user_metadata
    TO cwa_ppdd_retention;
