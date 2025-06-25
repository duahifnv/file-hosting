CREATE TABLE shared_files
(
    id          UUID PRIMARY KEY,
    metadata_id UUID        NOT NULL REFERENCES file_metadata (id),
    share_mode  VARCHAR(50) NOT NULL,
    shared_at   TIMESTAMPTZ NOT NULL,
    expires_at  TIMESTAMPTZ
);

CREATE TABLE shared_files_users
(
    id BIGSERIAL PRIMARY KEY,
    shared_id UUID NOT NULL REFERENCES shared_files(id),
    user_id UUID NOT NULL REFERENCES users(id)
)