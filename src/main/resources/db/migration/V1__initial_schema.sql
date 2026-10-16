-- Baseline schema for Aleph-Zero.
-- The database itself is created outside Flyway (docker-compose MYSQL_DATABASE,
-- a manual CREATE DATABASE, or the JDBC URL), so it is intentionally not named
-- here: every statement runs in the connection's default schema ("alephzero").
-- Dates are kept as TIMESTAMP to match the JPA entities.

-- User roles
CREATE TABLE user_role
(
    id_role     INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name        VARCHAR(20) UNIQUE NOT NULL,
    description VARCHAR(100)
);

-- Users
CREATE TABLE `user`
(
    id        INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    public_id VARCHAR(36)  NOT NULL UNIQUE,   -- UUID for public exposure
    username  VARCHAR(45)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,          -- password hash
    email     VARCHAR(200) NOT NULL UNIQUE,
    role_id   INTEGER      NOT NULL,

    CONSTRAINT user_role_fk FOREIGN KEY (role_id) REFERENCES user_role (id_role)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- User preferences (currently unused)
CREATE TABLE user_preferences
(
    id_settings        INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id            INTEGER NOT NULL,
    user_preferences   JSON,
    canvas_preferences JSON,

    CONSTRAINT user_preferences_user_fk FOREIGN KEY (user_id) REFERENCES `user` (id)
);

-- Stored math expressions
CREATE TABLE math_expression
(
    id          INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    expression  VARCHAR(255) NOT NULL,
    evaluation  LONGTEXT,
    calculation LONGTEXT,
    points      LONGTEXT,
    preferences LONGTEXT
);

-- Per-user history of expressions
CREATE TABLE user_history
(
    id         INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id    INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    snapshot   VARCHAR(36),                   -- UUID for image snapshot

    CONSTRAINT user_history_user_fk FOREIGN KEY (user_id) REFERENCES `user` (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE history_expression
(
    id                 INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    index_order        INTEGER DEFAULT 0,
    user_history_id    INTEGER NOT NULL,
    math_expression_id INTEGER NOT NULL,

    CONSTRAINT history_expr_history_fk FOREIGN KEY (user_history_id) REFERENCES user_history (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT history_expr_expr_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Share links (not yet implemented)
CREATE TABLE share_link
(
    id                      INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    token                   VARCHAR(64) UNIQUE NOT NULL, -- UUID
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at              TIMESTAMP NULL,
    math_expression_id      INTEGER NOT NULL,
    math_expression_user_id INTEGER NOT NULL,

    CONSTRAINT share_link_expression_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id),
    CONSTRAINT share_link_user_fk FOREIGN KEY (math_expression_user_id) REFERENCES `user` (id)
);
