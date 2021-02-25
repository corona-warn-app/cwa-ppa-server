# Event-Driven User Survey (EDUS) Service

This service will expose an API for handling One Time Passwords (OTPs). The role of this service is
to redeem OTPs and to generate OTPs for testing purposes. During productive usage, the OTPs are
created solely by the [PPAC service](PPAC.md).

## External Dependencies

- **Vault**: Used for secrets and certificate storage
- **RDBMS**: PostgreSQL as the persistent storage for notifications

## Environment Variables

EDUS has no service-specific environment variables.

## Spring Profiles

Spring profiles are used to apply federation key download service configuration based on the running
environment, determined by the active profile.

You will find `.yaml` based profile-specific configuration files
at [`/services/edus/src/main/resources`](/services/edus/src/main/resources).

### Available Profiles

Profile             | Effect
--------------------|-------------
`debug`             | Sets the log level to `DEBUG`.
`cloud`             | Removes default values for the `spring.flyway`, `spring.datasource`.
`generate-otp`      | Enables the `GenerateOtpController` that can be used to generate OTPs for testing purposes.

Please refer to the inline comments in the base `application.yaml` configuration file for further
details on the configuration properties impacted by the above profiles.

## OtpController

REST-controller that handles incoming GET-request for the redemption of OTPs. It is reading the OTP
defined in the `OtpRedemptionRequest` from the database and triggers its redemption in the
`OtpService`. Depending on the OTP state, the `OtpRedemptionResponse` is constructed accordingly and
sent back to the client.

## GenerateOtpController

This REST-controller is used for testing purposed only is solely active when spring
profile `generate-otp` is enabled. It offers and endpoint that allows to create a specified amount
of OTPs in the database that have the specified validity in hours. The response will be a list
of `OtpTestGenerationResponse`, which will result in a list of the generated OTPs together with
their expiration time.

## OtpControllerExceptionHandler

Responsible for general error handling within the EDUS service.

## SecurityConfig

The `SecurityConfig` is responsible for authenticated access to the resources provided by the
package. It allows only authenticated requests to the controllers explained above and makes sure
that the Actuator endpoints are available for the platform infrastructure. All other requests are
being denied.
