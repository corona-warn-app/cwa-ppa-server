-- see metrics.proto

CREATE TABLE int_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val BIGINT NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE text_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val TEXT NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE float_data (
    os VARCHAR(10) NOT NULL,
    metric INTEGER NOT NULL,
    m_val DOUBLE PRECISION NOT NULL,
    m_day DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE one_time_password (
    password VARCHAR(36) PRIMARY KEY,
    creation_timestamp TIMESTAMP NOT NULL,
    redemption_timestamp TIMESTAMP,
    last_validity_check_timestamp TIMESTAMP
);

CREATE TABLE api_token (
    api_token VARCHAR(32) PRIMARY KEY,
    expiration_date DATE NOT NULL,
    last_used_edus BIGINT,
    last_used_ppac BIGINT
);
