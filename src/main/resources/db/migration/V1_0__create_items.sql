CREATE TABLE items
(
    id          BIGSERIAL    NOT NULL,
    title       VARCHAR(256) NOT NULL,
    description VARCHAR(1024),
    img_path    VARCHAR(1024),
    count       INTEGER,
    price       DECIMAL(10, 2),
    CONSTRAINT items_pk PRIMARY KEY (id)
);