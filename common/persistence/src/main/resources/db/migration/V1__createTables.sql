-- see metrics.proto

CREATE TABLE data_donation.int_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val BIGINT NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE data_donation.text_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val TEXT NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE data_donation.float_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val DOUBLE PRECISION NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE data_donation.one_time_password (
    password VARCHAR(36) PRIMARY KEY,
    creation_timestamp BIGINT NOT NULL,
    redemption_timestamp BIGINT,
    last_validity_check_timestamp BIGINT
);

CREATE TABLE data_donation.api_token (
    api_token VARCHAR PRIMARY KEY,
    expiration_date BIGINT NOT NULL,
    created_at BIGINT NOT NULL,
    last_used_edus BIGINT,
    last_used_ppac BIGINT
);

CREATE TABLE data_donation.device_token (
  id SERIAL PRIMARY KEY,
  device_token_hash  BYTEA UNIQUE NOT NULL,
  created_at BIGINT NOT NULL
);



CREATE TABLE salt (
    salt VARCHAR(32) PRIMARY KEY,
    created_at bigint NOT NULL
);
