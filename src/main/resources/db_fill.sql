INSERT INTO USERS(username,password,enabled)
VALUES ('admin','admin', true);
INSERT INTO USER_ROLE (u_id, role)
VALUES ((SELECT U_ID FROM USERS WHERE USERNAME='admin'), 'GUEST');
INSERT INTO USER_ROLE (u_id, role)
VALUES ((SELECT U_ID FROM USERS WHERE USERNAME='admin'), 'ADMIN');

INSERT INTO USERS(username,password,enabled)
VALUES ('usr','123', true);
INSERT INTO USER_ROLE (u_id, role)
VALUES ((SELECT U_ID FROM USERS WHERE USERNAME='usr'), 'GUEST');