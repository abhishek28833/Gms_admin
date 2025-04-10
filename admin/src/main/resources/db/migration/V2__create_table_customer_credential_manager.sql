CREATE TYPE role_enum AS ENUM ('ADMIN', 'USER');


CREATE TABLE IF NOT EXISTS customer_credential_manager (
    id SERIAL PRIMARY KEY,
    mobile_number VARCHAR(15) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role role_enum NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ default current_timestamp,
    updated_at TIMESTAMPTZ default current_timestamp

);
