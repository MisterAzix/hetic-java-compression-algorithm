CREATE TABLE chunks (
    hash VARCHAR(255) PRIMARY KEY,
    content BYTEA
);

CREATE TABLE file_chunks (
    file_identifier VARCHAR(255),
    chunk_hash VARCHAR(255),
    FOREIGN KEY (chunk_hash) REFERENCES chunks(hash)
);