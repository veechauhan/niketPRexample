# --- !Ups
CREATE TABLE users
(
    id                 VARCHAR(50) PRIMARY KEY,
    name               VARCHAR(50) UNIQUE NOT NULL,
    email              VARCHAR(50) UNIQUE NOT NULL,
    mobile_number      VARCHAR(50),
    bio                VARCHAR(50),
    manager_id         VARCHAR(50),
    github_id          VARCHAR(50),
    role               VARCHAR(50)        NOT NULL,
    project_id         VARCHAR(50),
    studio_id          VARCHAR(50),
    disabled_timestamp INTEGER DEFAULT 0
);

CREATE TABLE projects
(
    id                 VARCHAR(50) PRIMARY KEY,
    project_name       VARCHAR(50) UNIQUE NOT NULL,
    project_info       VARCHAR(50)        NOT NULL,
    studio_id          VARCHAR(50)        NOT NULL,
    disabled_timestamp INTEGER DEFAULT 0
);

CREATE TABLE studios
(
    id                 VARCHAR(50) PRIMARY KEY,
    studio_name        VARCHAR(50) UNIQUE NOT NULL,
    studio_manager_id  VARCHAR(50)        NOT NULL,
    disabled_timestamp INTEGER DEFAULT 0
);

CREATE TABLE contributions
(
    id                 VARCHAR(50) PRIMARY KEY,
    contribution_name   VARCHAR(100) NOT NULL,
    contribution_url    VARCHAR     NOT NULL,
    created_on         numeric     NOT NULL,
    last_update_on     numeric     NOT NULL,
    user_id            VARCHAR(50) NOT NULL,
    studio_id          VARCHAR(50) NOT NULL,
    contribution_type  VARCHAR(50) NOT NULL,
    status             VARCHAR(50)  NOT NULL,
    remarks            jsonb       NOT NULL,
    disabled_timestamp INTEGER DEFAULT 0
);

# --- !Downs
DROP TABLE users;
DROP TABLE projects;
DROP TABLE studios;
DROP TABLE contributions;
