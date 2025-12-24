CREATE TABLE users
(
    id       BIGSERIAL NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    CONSTRAINT users_pk PRIMARY KEY (id)
);