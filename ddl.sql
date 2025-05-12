DROP SCHEMA IF EXISTS mysqldb;
CREATE SCHEMA IF NOT EXISTS mysqldb;
USE mysqldb;

-- Creacion de tablas.

-- User roles table
CREATE TABLE IF NOT EXISTS user_role
(
    id_role     INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name        VARCHAR(20)         NOT NULL,
    description VARCHAR(50)
);

-- User table
CREATE TABLE IF NOT EXISTS user(
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username VARCHAR(45) NOT NULL UNIQUE,
    email VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,

    -- FK
    CONSTRAINT user_role_fk FOREIGN KEY(role_id) REFERENCES user_role(id_role)
);

-- Table for user preferences
CREATE TABLE IF NOT EXISTS user_preferences (
    id_settings INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    user_preferences JSON,
    canvas_preferences JSON,

    -- FK
    CONSTRAINT user_preferences_user_fk FOREIGN KEY(user_id) REFERENCES user(id)
);

-- Table for each math lateXResultEvaluation
CREATE TABLE IF NOT EXISTS math_expression(
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    expression VARCHAR(255),
    points_snapshot JSON,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    -- FK
    CONSTRAINT expression_user_fk FOREIGN KEY (user_id) REFERENCES user (id)
);

-- Preferences for one math lateXResultEvaluation representation canvas.
CREATE TABLE IF NOT EXISTS expression_preferences(
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    use_global_preferences BOOLEAN,
    canvas_preferences JSON,
    math_expression_id INTEGER NOT NULL,
    math_expression_user_id INTEGER NOT NULL,

    -- FK
    CONSTRAINT expression_preferences_user_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id),
    CONSTRAINT expression_expression_fk FOREIGN KEY (math_expression_id) REFERENCES user (id)
);

-- Table to store share links
CREATE TABLE IF NOT EXISTS share_link(
    id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    math_expression_id INTEGER NOT NULL,
    math_expression_user_id INTEGER NOT NULL,

    -- FK
    CONSTRAINT share_link_user_fk FOREIGN KEY (math_expression_id) REFERENCES math_expression (id),
    CONSTRAINT share_link_expression_fk FOREIGN KEY (math_expression_id) REFERENCES user (id)
);