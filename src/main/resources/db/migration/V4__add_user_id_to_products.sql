ALTER TABLE order_ledger_products
    ADD COLUMN user_id BIGINT ,
    ADD CONSTRAINT fk_products_user FOREIGN KEY (user_id) REFERENCES order_ledger_users(id);
ALTER TABLE order_ledger_products
    DROP CONSTRAINT fk_products_user;

ALTER TABLE order_ledger_products
    DROP COLUMN user_id;

-- 1. Nullable əlavə et
ALTER TABLE order_ledger_products
    ADD COLUMN user_id BIGINT;

-- 2. Mövcud sətirlərə dəyər ver
UPDATE order_ledger_products
SET user_id = 1; -- və ya uyğun istifadəçi ID-si

-- 3. NOT NULL et
ALTER TABLE order_ledger_products
    ALTER COLUMN user_id SET NOT NULL;

-- 4. Foreign key əlavə et
ALTER TABLE order_ledger_products
    ADD CONSTRAINT fk_products_user
        FOREIGN KEY (user_id)
            REFERENCES order_ledger_users(id);