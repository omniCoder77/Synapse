set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE auth_db;
    CREATE DATABASE order_db;
    CREATE DATABASE payment;

    CREATE TABLE users(
    username                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    password                 VARCHAR(255)             NOT NULL,
    email                    VARCHAR(255)             NOT NULL UNIQUE,
    roles                    VARCHAR(255)             NOT NULL DEFAULT '',
    is_account_locked        BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_account_enabled       BOOLEAN                  NOT NULL DEFAULT FALSE,
    enabled                  BOOLEAN                  NOT NULL DEFAULT FALSE,
    mfa                      BOOLEAN                  NOT NULL DEFAULT FALSE,
    phone_number             VARCHAR(20)              NOT NULL DEFAULT '',
    email_verified_at        TIMESTAMP WITH TIME ZONE,
    phone_number_verified_at TIMESTAMP WITH TIME ZONE,
    totp                     VARCHAR(255),
    refresh_token            VARCHAR(255)             NOT NULL DEFAULT '',
    version                  BIGINT                   NOT NULL DEFAULT 0,
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
    );
    CREATE TABLE login_attempts(
    username           UUID PRIMARY KEY REFERENCES users (username),
    last_login_attempt TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    attempt            INT                      NOT NULL DEFAULT 0,
    device_fingerprint TEXT[]                   NOT NULL DEFAULT '{}'
    );

    CREATE TABLE outbox(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255)             NOT NULL,
    aggregate_id   UUID                     NOT NULL,
    event_type     VARCHAR(255)             NOT NULL,
    payload        TEXT                    NOT NULL,
    headers        TEXT,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    processed_at   TIMESTAMP WITH TIME ZONE,
    status         VARCHAR(50)              NOT NULL DEFAULT 'PENDING'
    );

EOSQL
