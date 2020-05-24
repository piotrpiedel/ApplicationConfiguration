INSERT INTO ROLE (ID,NAME) VALUES
  (1, 'admin'),
  (2, 'user');

INSERT INTO USER (ID, USER_NAME, PASSWORD, ROLE_ID) VALUES
  (1, 'adminTest', 'admin1', 1),
  (2, 'userTest', 'user1', 2);