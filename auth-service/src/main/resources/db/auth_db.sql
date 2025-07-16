CREATE TABLE login_attempts
(
    username           uuid PRIMARY KEY,
    last_login_attempt timestamp with time zone DEFAULT current_timestamp,
    attempt            smallint                 default 0,
    device_fingerprint TEXT[]                   DEFAULT '{}'
);

CREATE TABLE users
(
    username                 uuid PRIMARY KEY                  DEFAULT gen_random_uuid(),
    password                 VARCHAR(255)             NOT NULL,
    email                    VARCHAR(255)             NOT NULL UNIQUE,
    roles                    VARCHAR(255)             NOT NULL DEFAULT '',
    is_account_locked        BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_account_enabled       BOOLEAN                  NOT NULL DEFAULT FALSE,
    enabled                  BOOLEAN                  NOT NULL DEFAULT FALSE,
    mfa                      BOOLEAN                  NOT NULL DEFAULT FALSE,
    phone_number             VARCHAR(50)              NOT NULL DEFAULT '',
    email_verified_at        TIMESTAMP WITH TIME ZONE,
    phone_number_verified_at TIMESTAMP WITH TIME ZONE,
    totp                     VARCHAR(255),
    refresh_token            TEXT                     NOT NULL DEFAULT '',
    version                  BIGINT                   NOT NULL DEFAULT 0,
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMIT;