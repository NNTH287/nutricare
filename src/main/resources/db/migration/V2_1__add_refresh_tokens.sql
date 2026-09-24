CREATE TABLE refresh_token (
    jti             UUID PRIMARY KEY,
    user_id         INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    issued_at       TIMESTAMP NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    revoked_at      TIMESTAMP,
    replaced_by_jti UUID
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
