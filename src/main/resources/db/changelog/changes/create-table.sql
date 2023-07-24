CREATE TABLE IF NOT EXISTS captcha
(
    id              SERIAL PRIMARY KEY,
    image           BYTEA     NOT NULL,
    label           TEXT      NOT NULL,
    predicted_value TEXT,
    filename        TEXT      NOT NULL UNIQUE,
    format          TEXT      NOT NULL,
    width           INTEGER   NOT NULL,
    height          INTEGER   NOT NULL,
    sample          BOOLEAN   NOT NULL,
    created_at      TIMESTAMP NOT NULL
);
