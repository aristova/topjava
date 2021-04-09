DELETE
FROM user_roles;
DELETE
FROM users;
DELETE
FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (datetime, description, calories, user_id)
VALUES ('2021-01-01 09:00:00', 'Завтрак', 300, 100000),
       ('2021-01-05 14:00:00', 'Обед', 400, 100000),
       ('2021-01-05 20:00:00', 'Ужин', 500, 100000),
       ('2021-01-02 00:00:00', 'Еда на граничное значение', 100, 100001),
       ('2021-01-02 10:00:00', 'Завтрак', 1000, 100001),
       ('2021-01-02 15:00:00', 'Обед', 500, 100001),
       ('2021-01-02 19:00:00', 'Ужин', 410, 100001);