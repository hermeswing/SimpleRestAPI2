COMMENT on column USERS.ID is 'ID';
COMMENT on column USERS.USER_ID is '사용자 ID';
COMMENT on column USERS.USER_NM is '사용자명';
COMMENT on column USERS.PASSWORD is '비밀번호';
COMMENT on column USERS.EMAIL is '이메일주소';
COMMENT on column USERS.USER_ROLE is '사용자권한';
COMMENT on column USERS.CRT_ID is '생성자ID';
COMMENT on column USERS.CRT_DT is '생성일시';
COMMENT on column USERS.MDF_ID is '수정자ID';
COMMENT on column USERS.MDF_DT is '수정일시';

INSERT INTO USERS(USER_ID, USER_NM, PASSWORD, EMAIL, USER_ROLE, CRT_ID, CRT_DT, MDF_ID, MDF_DT) VALUES ('admin', '어드민', '$2a$10$O89WqQ2Lskyl/wL5DrKj/eX1ZxalpBCQoFz8CsUKBLl/C4mfNSrge', 'admin@naver.com', 'ADMIN', 'admin', NOW(), 'admin', NOW());
INSERT INTO USERS(USER_ID, USER_NM, PASSWORD, EMAIL, USER_ROLE, CRT_ID, CRT_DT, MDF_ID, MDF_DT) VALUES ('hong', '홍길동', '$2a$10$O89WqQ2Lskyl/wL5DrKj/eX1ZxalpBCQoFz8CsUKBLl/C4mfNSrge', 'hong@naver.com', 'USER', 'admin', NOW(), 'admin', NOW());
