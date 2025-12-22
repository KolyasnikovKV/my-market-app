CREATE TABLE orders
(
    id      BIGSERIAL NOT NULL,
    user_id BIGSERIAL NOT NULL,
    CONSTRAINT orders_pk PRIMARY KEY (id),
    CONSTRAINT fk_orders_to_users FOREIGN KEY (user_id) REFERENCES orders (id)
);

CREATE TABLE order_details
(
    id      BIGSERIAL NOT NULL,
    order_id BIGSERIAL NOT NULL,
    item_id  BIGSERIAL NOT NULL,
    title       VARCHAR(256),
    description VARCHAR(1024),
    count    INTEGER   NOT NULL,
    CONSTRAINT order_details_pk PRIMARY KEY (id),
    CONSTRAINT fk_orders_items_to_orders FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_orders_items_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);