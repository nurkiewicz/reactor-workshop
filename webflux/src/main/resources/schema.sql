CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(256) PRIMARY KEY,
    password VARCHAR(256),
    role VARCHAR(256)
);