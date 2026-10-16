-- Refresh tokens: the stateless authentication addition.
-- One active token per user, rotated on every refresh (only the SHA-256 hash of
-- the opaque token is stored; the raw value lives in the user's HttpOnly cookie).

CREATE TABLE refresh_token
(
    id         INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id    INTEGER     NOT NULL UNIQUE,   -- only 1 active refresh token per user
    token_hash VARCHAR(64) NOT NULL UNIQUE,   -- SHA-256 hex of the opaque token
    expires_at TIMESTAMP   NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT refresh_token_user_fk FOREIGN KEY (user_id) REFERENCES `user` (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
