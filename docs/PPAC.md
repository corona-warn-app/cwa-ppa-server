# Privacy-Preserving Access Control (PPAC) Service

The concept for Privacy-preserving Access Control (PPAC) ensures that respective APIs for Event Driven
User Survey (EDUS) and Privacy Preserving Analytics (PPA) are restricted and protected against large-scale abuse.
The CWA Data Donation Server (Data Donation Server) uses PPAC before processing any requests related to EDUS or PPA.

With PPAC, the client collects an authenticity proof of the device and/or CWA app and sends it to the Data Donation
Server for verification. If the verification of the authenticity proof passes, the server continues to process the
request. Otherwise, the request is rejected.

The authenticity proof is OS-specific and uses native capabilities:
 * iOS: leverages the Device Identification API to authorize an API Token for the current month
 * Android: leverages the SafetyNet Attestation API to provide details on how authentic the device and client are

The payload to be sent by the iOS mobile applications is defined in the [ppa_data_request_ios.proto](../common/protocols/src/main/proto/app/coronawarn/datadonation/common/protocols/internal/ppdd/ppa_data_request_ios.proto)
```
    message PPADataRequestIOS {

      PPACIOS authentication = 1;

      PPADataIOS payload = 2;
    }
```

The payload to be sent by the Android mobile applications is defined in the [ppa_data_request_android.proto](../common/protocols/src/main/proto/app/coronawarn/datadonation/common/protocols/internal/ppdd/ppa_data_request_android.proto)
```
message PPADataRequestAndroid {

  PPACAndroid authentication = 1;

  PPADataAndroid payload = 2;
}
```
Additionally, the endpoints require the following headers to be set:
```
Headers iOS
Content-Type: application/x-protobuf
cwa-ppac-ios-accept-api-token: false[default]
```

```
Headers Android
Content-Type: application/x-protobuf
```

## iOS: Privacy-preserving Access Control
On iOS, the Device Identification API is leveraged to realize PPAC.
The Device Identification API allows to check that a request originates from a genuine iOS device.

Realization of PPAC: on iOS is realized by leveraging the Device Identification API to authorize an API Token for the current month.

Server side steps:
1. Use provided device token to query per device data from Apple Device Identification API[ppac_ios.proto](../common/protocols/src/main/proto/app/coronawarn/datadonation/common/protocols/internal/ppdd/ppac_ios.proto)
    ```
    message PPACIOS {

      string deviceToken = 1;

      string apiToken = 2;
    }
    ```
2. The provided API token is used in the following validation steps:
    * The API Token should be unknown to the server and the last_update_time attribute of the per-device data should not(!) be the current month (UTC).
    * The API Token should not be yet expired.
    * The API Token should pass the rate limit check.

3. If all validation steps passed then the payload is valid and it would be saved.


## Android: Privacy-preserving Access Control
On Android, the SafetyNet Attestation API is leveraged to realize PPAC.
The SafetyNet Attestation API allows to check that a request originates from a genuine Android device
(e.g. fails in device emulators), whether there is any trace of a manipulated device (e.g. rooted device),
and whether the integrity of the client is intact (e.g. whether the client was modified).

Realization of PPAC: on Android is realized by leveraging the SafetyNet Attestation API and assessing the authenticity of the client.

Server side:

## External Dependencies

- **Vault**: Used for secrets and certificate storage
- **RDBMS**: PostgreSQL as the persistent storage for notifications
- **Apple Device Identification API** Allows to check that a request originates from a genuine iOS device
- **Google SafetyNet Attestation API** Allows to check that a request originates from a genuine Android device

## Environment Variables iOS
| Name | Description |
|----------------------------------------- |---------------------------------------------------------------------------------------------------- |
| `PPAC_IOS_DEVICE_TOKEN_MIN_LENGTH` | Minimum length for the Device Token. |
| `PPAC_IOS_DEVICE_TOKEN_MAX_LENGTH` | Maximum length for the Device Token. |
| `PPAC_IOS_JWT_KEY_ID` | Key Id for JWT generation. |
| `PPAC_IOS_JWT_TEAM_ID` | Team Id for JWT generation. |
| `PPAC_IOS_JWT_SIGNING_KEY` | Signing Key for JWT generation. |


## Environment Variables Android
| Name | Description |
|----------------------------------------- |---------------------------------------------------------------------------------------------------- |
| `PPAC_ANDROID_CERTIFICATE _HOSTNAME` | Hostname for certificate verification. |
| `PPAC_ANDROID_ATTESTATION_VALIDITY_IN_SECONDS` | Validity of the attestation represented in seconds. |
| `PPAC_ANDROID_ALLOWED_APK_PACKAGE_NAMES` | An array of valid APK package names. |
| `PPAC_ANDROID_ALLOWED_APK_CERTIFICATE_DIGESTS` | An array of valid APK certificate digests (base64-encoded). |

## Spring profiles

Spring profiles are used to apply ppac service configuration based on the running environment.

### Available Profiles

Profile                                           | Effect
--------------------------------------------------|-------------
`debug`                                           | Sets the log level to `DEBUG` and changes the `CONSOLE_LOG_PATTERN` used by Log4j 2.
`cloud`                                           | Updates values for the `spring.flyway`, `spring.datasource`.
`disable-ssl-client-postgres`                     | Disables SSL with a pinned certificate for the connection to the postgres.
`test`                                            | Enable the usage of the header `cwa-ppac-ios-accept-api-token`.
`loadtest`                                        | iOS: loadtest profile removes ApiTokenAuthentication; It will also not thrown errors during validation/update; Disables per device data validation; Disables device toke redemption; Disables rate limit check. Android: Disables all validation checks.
`!loadtest`                                       | Enables all validations. This is the profile used in PROD.


Please refer to the inline comments in the base `application.yaml` configuration file for further details on the configuration properties impacted by the above profiles.

## SecurityConfig

The `SecurityConfig` is responsible for authenticated access to the resources provided by the
package. It allows only authenticated requests to the controllers explained above and makes sure
that the Actuator endpoints are available for the platform infrastructure. All other requests are
being denied.
