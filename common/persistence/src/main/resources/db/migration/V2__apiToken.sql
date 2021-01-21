CREATE TABLE api_token (
    api_token VARCHAR(32) PRIMARY KEY,
    expiration_date DATE NOT NULL,
    last_used_edus BIGINT,
    last_used_ppac BIGINT
);
