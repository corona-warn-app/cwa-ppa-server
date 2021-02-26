# Retention Service

This service is responsible for the periodic database table clean-up.

## External Dependencies

- **Vault**: Used for secrets and certificate storage
- **RDBMS**: PostgreSQL as the persistent storage for notifications

## Environment Variables

| Name | Description |
|----------------------------------------- |---------------------------------------------------------------------------------------------------- |
| `EXPOSURE_RISK_METADATA_RETENTION_DAYS` | Data retention time in the `exposure_risk_metadata` table expressed in days. |
| `EXPOSURE_WINDOW_RETENTION_DAYS` | Data retention time in the `exposure_window` table expressed in days. |
| `KEY_METADATA_WITH_CLIENT_RETENTION_DAYS` | Data retention time in the `key_submission_metadata_with_client_metadata` table expressed in days. |
| `KEY_METADATA_WITH_USER_RETENTION_DAYS` | Data retention time in the `key_submission_metadata_with_user_metadata` table expressed in days. |
| `TEST_RESULT_METADATA_RETENTION_DAYS` | Data retention time in the `test_result_metadata` table expressed in days. |
| `OTP_RETENTION_DAYS` | Data retention time in the `one_time_password` table expressed in days. |
| `API_TOKEN_RETENTION_DAYS` | Data retention time in the `api_token` table expressed in days.  |
| `DEVICE_TOKEN_RETENTION_HOURS` | Data retention time in the `device_token` table expressed in hours. |
| `SALT_RETENTION_HOURS` | Data retention time in the `salt` table expressed in hours. |
| `CLIENT_METADATA_RETENTION_DAYS` | Data retention time in the `client_metadata` table expressed in days. |

## Spring Profiles

Spring profiles are used to apply retention service configuration based on the running environment, determined by the active profile.

You will find `.yaml` based profile-specific configuration files
at [`/services/retention/src/main/resources`](/services/retention/src/main/resources).

Profile                            | Effect
-----------------------------------|-------------
`debug`                            | Sets the log level to `DEBUG`.
`cloud`                            | Removes default values for the `spring.flyway`, `spring.datasource`.
`disable-ssl-client-postgres`      | Disables SSL for the connection to the postgres database.
`test`                             | Enables test data generation. (used for integration tests)

Please refer to the inline comments in the base `application.yaml` configuration file for further
details on the configuration properties impacted by the above profiles.

## RetentionPolicy

This main component is used to perform the database clean-up. It deletes every entry in the tables based on the configured
retention period and is the first component executed after the application start-up.
The retention period is different for each table depending on the configured environment variables described earlier.
The rules are the following:

| Table | Column | Rule |
|---------------------------------------------- |--------------------------------------------	|---------------------------------------------------------------------------------------------------------|
| `exposure_risk_metadata`| `submitted_at` | Delete if the column is older than the value configured by the `EXPOSURE_RISK_METADATA_RETENTION_DAYS`. |
| `exposure_window` | `submitted_at` | Delete if the column is older than the value configured by the `EXPOSURE_WINDOW_RETENTION_DAYS`. |
| `key_submission_metadata_with_client_metadata` | `submitted_at` | Delete if the column is older than the value configured by the `KEY_METADATA_WITH_CLIENT_RETENTION_DAYS`. |
| `key_submission_metadata_with_user_metadata` | `submitted_at` | Delete if the column is older than the value configured by the `KEY_METADATA_WITH_USER_RETENTION_DAYS`. |
| `test_result_metadata` | `submitted_at` | Delete if the column is older than the value configured by the `TEST_RESULT_METADATA_RETENTION_DAYS`. |
| `one_time_password` | `redemption_timestamp`, `expiration_timestamp` | Delete if one of the columns is older than the value configured by the `OTP_RETENTION_DAYS`. |
| `api_token` | `expiration_date` | Delete if the column is older than the value configured by the `API_TOKEN_RETENTION_DAYS`. |
| `device_token` | `created_at` | Delete if the column is older than the value configured by the `DEVICE_TOKEN_RETENTION_HOURS`. |
| `salt` | `created_at` | Delete if the column is older than the value configured by the `SALT_RETENTION_HOURS`. |
| `client_metadata` | `submitted_at` | Delete if the column is older than the value configured by the `CLIENT_METADATA_RETENTION_DAYS`. |
