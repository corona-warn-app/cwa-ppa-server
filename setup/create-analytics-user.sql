CREATE ROLE cwa_ppdd_analytics
  NOLOGIN
  NOSUPERUSER
  NOINHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  IN ROLE cwa_ppdd_user;

GRANT CONNECT ON DATABASE "cwa-data" TO cwa_ppdd_analytics;

GRANT SELECT ON TABLE data_donation.exposure_risk_metadata,
 data_donation.test_result_metadata,
 data_donation.key_submission_metadata_with_user_metadata,
 data_donation.key_submission_metadata_with_client_metadata,
 data_donation.user_metadata,
 data_donation.client_metadata,
 data_donation.scan_instance,
 data_donation.exposure_window,
 data_donation.flyway_schema_history TO cwa_ppdd_analytics;

CREATE USER "ppdd_analytics_user" WITH INHERIT IN ROLE cwa_ppdd_analytics ENCRYPTED PASSWORD '<change me>';

GRANT USAGE ON SCHEMA data_donation TO ppdd_analytics_user;
