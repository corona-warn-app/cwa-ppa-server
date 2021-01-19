CREATE TABLE api_token (
    api_token VARCHAR(32) PRIMARY KEY,
    expiration_date timestamptz NOT NULL,
    last_used_edus timestamptz,
    last_used_ppac timestamptz
);