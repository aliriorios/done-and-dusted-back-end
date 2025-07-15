INSERT INTO users (id, username, password, role) VALUES
(1, 'admin@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_ADMIN'),
(2, 'bob@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(3, 'charlie@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(4, 'diana@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(5, 'edward@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(6, 'fiona@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(7, 'george@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(8, 'hannah@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(9, 'ivan@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT'),
(10, 'julia@email.com', '$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO', 'ROLE_CLIENT');

INSERT INTO clients (id, name, user_id) VALUES
(1, 'Admin', 1),
(2, 'Bob', 2),
(3, 'Charlie', 3),
(4, 'Diana', 4),
(5, 'Edward', 5),
(6, 'Fiona', 6),
(7, 'George', 7),
(8, 'Hannah', 8),
(9, 'Ivan', 9),
(10, 'Julia', 10);
