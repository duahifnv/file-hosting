CREATE TABLE users
(
    id       UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

CREATE TABLE file_metadata (
   id UUID PRIMARY KEY,
   user_id UUID NOT NULL REFERENCES users(id),
   original_name VARCHAR(255) NOT NULL,
   content_type VARCHAR(100),
   original_size BIGINT NOT NULL,
   bucket VARCHAR(50) NOT NULL,
   object_path VARCHAR(500) NOT NULL,
   encryption_key BYTEA NOT NULL,
   iv BYTEA NOT NULL,
   created_at TIMESTAMPTZ NOT NULL,
   expires_at TIMESTAMPTZ
);