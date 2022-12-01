#!/bin/bash

# whatever you've started locally
curl --cert certificate.crt --key private.pem --insecure https://localhost:8080/version/v1/gen/srs/10/30

# docker-compose up:

# srs-verify-1
curl -v --cert certificate.crt --key private.pem --insecure https://localhost:8105/version/v1/gen/srs/10/30

# edus-1
curl -v --cert certificate.crt --key private.pem --insecure https://localhost:8103/version/v1/gen/otp/10/30

# els-verify-1
curl -v --cert certificate.crt --key private.pem --insecure https://localhost:8106/version/v1/gen/els/10/30
