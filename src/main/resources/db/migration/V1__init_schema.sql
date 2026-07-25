-- 1. USERS TABLE
CREATE TABLE order_ledger_users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. PRODUCTS TABLE
CREATE TABLE order_ledger_products
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    price          NUMERIC(19, 2) NOT NULL,
    stock_quantity INT            NOT NULL CHECK (stock_quantity >= 0),
    version        BIGINT         NOT NULL DEFAULT 0
);

--  3. ORDERS TABLE
CREATE TABLE order_ledger_orders
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL CHECK (total_amount >= 0),
    status       VARCHAR(50)    NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

-- 4. ORDER_ITEMS TABLE
CREATE TABLE order_ledger_order_items
(
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT         NOT NULL,
    product_id        BIGINT         NOT NULL,
    quantity          INT            NOT NULL CHECK (quantity > 0),
    price_at_purchase NUMERIC(19, 2) NOT NULL CHECK (price_at_purchase > 0),
    subtotal          NUMERIC(19, 2) NOT NULL CHECK (subtotal > 0),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
);

-- 5. ORDER_STATUS_HISTORY TABLE
CREATE TABLE order_ledger_order_status_history
(
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT      NOT NULL,
    previous_status VARCHAR(50),
    new_status      VARCHAR(50) NOT NULL,
    reason          VARCHAR(255),
    timestamp       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_history_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);