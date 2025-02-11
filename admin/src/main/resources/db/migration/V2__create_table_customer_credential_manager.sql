CREATE TABLE IF NOT EXISTS customer_credential_manager (
    id SERIAL PRIMARY KEY,
    mobile_number VARCHAR(15) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ default current_timestamp,
    updated_at TIMESTAMPTZ default current_timestamp

);
