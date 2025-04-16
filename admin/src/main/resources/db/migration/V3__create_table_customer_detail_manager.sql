CREATE TABLE IF NOT EXISTS customer_detail_manager (
    user_id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    username VARCHAR(100),
    email VARCHAR(255),
    address TEXT,
    role_type INTEGER,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_type FOREIGN KEY (role_type) REFERENCES master_gms_roles(id)
);
