CREATE SCHEMA IF NOT EXISTS mysqldb;
USE mysqldb;

-- User roles table
CREATE TABLE IF NOT EXISTS user_role
(
    id_role     INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name        VARCHAR(20) UNIQUE NOT NULL,
    description VARCHAR(100)
);

INSERT IGNORE INTO user_role (name, description)
VALUES
    ('ADMIN', 'Administrator with full access rights'),
    ('USER', 'Regular user with limited access rights');

-- User table
CREATE TABLE IF NOT EXISTS user
(
    id        INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    public_id VARCHAR(36)         NOT NULL UNIQUE,     -- UUid for public exposure.
    username  VARCHAR(45)         NOT NULL UNIQUE,
    password  VARCHAR(255)        NOT NULL,  -- password hash.
    email     VARCHAR(200)        NOT NULL UNIQUE,
    role_id   INTEGER             NOT NULL,

    -- FK
    CONSTRAINT user_role_fk FOREIGN KEY (role_id) REFERENCES user_role (id_role)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Table for user preferences (DISCARDED FOR NOW)
CREATE TABLE IF NOT EXISTS user_preferences
(
    id_settings        INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id            INTEGER             NOT NULL,
    user_preferences   JSON,
    canvas_preferences JSON,

    -- FK
    CONSTRAINT user_preferences_user_fk FOREIGN KEY (user_id) REFERENCES user (id)
);

-- Table for each math lateXResultEvaluation
CREATE TABLE IF NOT EXISTS math_expression
(
    id              INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    expression      VARCHAR(255)        NOT NULL,
    evaluation      LONGTEXT,
    calculation     LONGTEXT,
    points          LONGTEXT, -- Points in the expression
    preferences     LONGTEXT -- Preferences in the expression as JSON (color, bound, origin, etc.)
);

CREATE TABLE IF NOT EXISTS user_history
(
    id         INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id    INTEGER             NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    snapshot   VARCHAR(36), -- UUID for image snapshot

    CONSTRAINT user_history_user_fk FOREIGN KEY (user_id) REFERENCES user (id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS history_expression
(
    id                 INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    index_order        INTEGER DEFAULT 0,
    user_history_id    INTEGER             NOT NULL,
    math_expression_id INTEGER             NOT NULL,

    CONSTRAINT history_expr_history_fk FOREIGN KEY (user_history_id) REFERENCES user_history (id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT history_expr_expr_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- Table to store share links (for future versions)
CREATE TABLE IF NOT EXISTS share_link
(
    id                      INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    token                   VARCHAR(64)         UNIQUE NOT NULL, -- UUid
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at              TIMESTAMP           NULL,
    math_expression_id      INTEGER             NOT NULL,
    math_expression_user_id INTEGER             NOT NULL,

    -- FK
    CONSTRAINT share_link_expression_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id),
    CONSTRAINT share_link_user_fk FOREIGN KEY (math_expression_user_id) REFERENCES user (id)
);