#!/bin/bash

curl --cert certificate.crt --key private.pem --insecure https://localhost:8080/version/v1/gen/srs/10/30
