ALTER TABLE order_ledger_coupons
    ADD COLUMN user_id BIGINT;

UPDATE order_ledger_coupons
SET user_id = 1
WHERE user_id IS NULL;

ALTER TABLE order_ledger_coupons
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE order_ledger_coupons
    ADD CONSTRAINT fk_coupons_user
        FOREIGN KEY (user_id) REFERENCES order_ledger_users(id);