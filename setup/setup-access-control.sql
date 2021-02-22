-- AC
GRANT SELECT, INSERT, UPDATE ON TABLE
  data_donation.api_token,
  data_donation.device_token,
  data_donation.exposure_risk_metadata,
  data_donation.exposure_window,
  data_donation.key_submission_metadata_with_client_metadata,
  data_donation.key_submission_metadata_with_user_metadata,
  data_donation.one_time_password,
  data_donation.salt,
  data_donation.scan_instance,
  data_donation.test_result_metadata,
  data_donation.client_metadata,
  data_donation.user_metadata
  TO cwa_ppdd_ppac;

GRANT ALL ON ALL SEQUENCES IN SCHEMA data_donation TO cwa_ppdd_ppac;

GRANT SELECT, INSERT, UPDATE ON TABLE
  data_donation.one_time_password
  TO cwa_ppdd_edus;

GRANT DELETE ON TABLE
    data_donation.api_token,
    data_donation.device_token,
    data_donation.exposure_risk_metadata,
    data_donation.exposure_window,
    data_donation.key_submission_metadata_with_client_metadata,
    data_donation.key_submission_metadata_with_user_metadata,
    data_donation.one_time_password,
    data_donation.salt,
    data_donation.scan_instance,
    data_donation.test_result_metadata,
    data_donation.client_metadata,
    data_donation.user_metadata
    TO cwa_ppdd_retention;
