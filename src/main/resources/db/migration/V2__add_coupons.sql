CREATE TABLE order_ledger_coupons (
                                      id BIGSERIAL PRIMARY KEY,
                                      code VARCHAR(50) NOT NULL UNIQUE,
                                      discount_percentage NUMERIC(5, 2) NOT NULL CHECK (discount_percentage > 0 AND discount_percentage <= 100),
                                      max_usage_limit INT NOT NULL CHECK (max_usage_limit >= 0),
                                      current_usage_count INT NOT NULL DEFAULT 0,
                                      expiration_date TIMESTAMP NOT NULL,
                                      is_active BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE order_ledger_orders
    ADD COLUMN coupon_id BIGINT,
    ADD COLUMN discount_amount NUMERIC(19, 2) DEFAULT 0.00,
    ADD CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES order_ledger_coupons(id);