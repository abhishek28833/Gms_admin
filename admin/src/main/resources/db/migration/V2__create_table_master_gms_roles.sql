CREATE TABLE IF NOT EXISTS master_gms_roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    code VARCHAR(50),
    description VARCHAR(255),
    created_at TIMESTAMPTZ default current_timestamp,
    updated_at TIMESTAMPTZ default current_timestamp
);
