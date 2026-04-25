CREATE TABLE IF NOT EXISTS email
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    to_email   VARCHAR(320) NOT NULL,
    subject    VARCHAR(999) NOT NULL,
    is_html    BOOLEAN      NOT NULL DEFAULT FALSE,
    body       TEXT         NOT NULL,
    status     SMALLINT     NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NULL
);
