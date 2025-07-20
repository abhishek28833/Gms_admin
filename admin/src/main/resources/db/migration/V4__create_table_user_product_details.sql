CREATE TABLE IF NOT EXISTS user_product_details (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255),
    product_description TEXT,
    product_price DOUBLE PRECISION,
    receipt_image_url VARCHAR(512),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_user_product_user
        FOREIGN KEY (user_id)
        REFERENCES customer_detail_manager(user_id)
        ON DELETE CASCADE
);