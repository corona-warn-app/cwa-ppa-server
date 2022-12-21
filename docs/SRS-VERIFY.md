# Self-Report Submission (SRS) Verification Service

This service will expose an API for handling SRS One Time Passwords (OTPs). The role of this service is
to redeem SRS OTPs and to generate SRS OTPs for testing purposes. During productive usage, the SRS OTPs are
created solely by the [PPAC service](PPAC.md).

## External Dependencies

- **Vault**: Used for secrets and certificate storage
- **RDBMS**: PostgreSQL as the persistent storage for notifications

## Environment Variables

SRS-VERIFY has no service-specific environment variables.

## Spring Profiles

You will find `.yaml` based profile-specific configuration files
at [`/services/srs-verify/src/main/resources`](/services/srs-verify/src/main/resources).

### Available Profiles

Profile              | Effect
---------------------|-------------
`debug`             | Sets the log level to `DEBUG`.
`cloud`             | Removes default values for the `spring.flyway`, `spring.datasource`.
`generate-srs-otp` | Enables the `GenerateSrsOtpController` that can be used to generate SRS OTPs for testing purposes.

Please refer to the inline comments in the base `application.yaml` configuration file for further
details on the configuration properties impacted by the above profiles.

## SrsOtpController

REST-controller that handles incoming GET-request for the redemption of SRS OTPs. It is reading the SRS OTP
defined in the `SrsOtpRedemptionRequest` from the database and triggers its redemption in the
`SrsOtpService`. Depending on the SRS OTP state, the `SrsOtpRedemptionResponse` is constructed accordingly and
sent back to the client.

## GenerateSrsOtpController

This REST-controller is used for testing purposed only is solely active when spring
profile `generate-srs-otp` is enabled. It offers and endpoint that allows to create a specified amount
of SRS OTPs in the database that have the specified validity in hours. The response will be a list
of `OtpTestGenerationResponse`, which will result in a list of the generated SRS OTPs together with
their expiration time.

## SrsOtpControllerExceptionHandler

Responsible for general error handling within the SRS-VERIFY service.

## SecurityConfig

The `SecurityConfig` is responsible for authenticated access to the resources provided by the
package. It allows only authenticated requests to the controllers explained above and makes sure
that the Actuator endpoints are available for the platform infrastructure. All other requests are
being denied.
