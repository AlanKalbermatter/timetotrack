-- TimeToTrack schema. Used by docker-compose (docker-entrypoint-initdb.d) and by the integration tests.

CREATE TABLE IF NOT EXISTS "user" (
    user_id       SERIAL PRIMARY KEY,
    username      TEXT        NOT NULL UNIQUE,
    email         TEXT        NOT NULL UNIQUE,
    full_name     TEXT        NOT NULL,
    password_hash TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS customer (
    customer_id   SERIAL PRIMARY KEY,
    customer_name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS projects (
    project_id   SERIAL PRIMARY KEY,
    project_name TEXT NOT NULL,
    customer_id  INT  NOT NULL REFERENCES customer (customer_id) ON DELETE RESTRICT,
    UNIQUE (customer_id, project_name)
);

CREATE TABLE IF NOT EXISTS time_entry (
    time_entry_id BIGSERIAL PRIMARY KEY,
    user_id       INT         NOT NULL REFERENCES "user" (user_id) ON DELETE CASCADE,
    project_id    INT         NOT NULL REFERENCES projects (project_id) ON DELETE RESTRICT,
    description   TEXT,
    from_time     TIMESTAMPTZ NOT NULL,
    to_time       TIMESTAMPTZ,
    CONSTRAINT time_entry_ends_after_start CHECK (to_time IS NULL OR to_time > from_time)
);

-- A user can have at most one running timer (to_time IS NULL).
CREATE UNIQUE INDEX IF NOT EXISTS one_running_timer_per_user ON time_entry (user_id) WHERE to_time IS NULL;
CREATE INDEX IF NOT EXISTS time_entry_user_from ON time_entry (user_id, from_time DESC);
