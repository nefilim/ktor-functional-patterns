CREATE TABLE IF NOT EXISTS users ( -- plural to avoid reserved word shenanigans :(
    id uuid,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    birth_date DATE NOT NULL,
    created_timestamp TIMESTAMP NOT NULL,
    last_updated_timestamp TIMESTAMP NOT NULL,
    CONSTRAINT PK_User_ID PRIMARY KEY (id)
);