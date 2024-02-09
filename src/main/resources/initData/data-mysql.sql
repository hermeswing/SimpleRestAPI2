alter table users comment = '사용자 관리';

alter table users modify id serial not null comment 'ID';
alter table users modify user_id varchar(20) not null comment '사용자 ID';
alter table users modify user_nm varchar(20) not null comment '사용자명';
alter table users modify password varchar(256) not null comment '비밀번호';
alter table users modify email varchar(200) not null comment '이메일주소';
alter table users modify user_role varchar(50) not null comment '사용자권한';
alter table users modify crt_id varchar(20) not null comment '생성자id';
alter table users modify crt_dt timestamp not null comment '생성일시';
alter table users modify mdf_id varchar(20) not null comment '수정자id';
alter table users modify mdf_dt timestamp not null comment '수정일시';

insert into users(user_id, user_nm, password, email, user_role, crt_id, crt_dt, mdf_id, mdf_dt) values ('admin', '어드민', '1234', 'admin@naver.com', 'ADMIN', 'admin', now(), 'admin', now());
insert into users(user_id, user_nm, password, email, user_role, crt_id, crt_dt, mdf_id, mdf_dt) values ('hong', '홍길동', '1234', 'hong@naver.com', 'USER', 'admin', now(), 'admin', now());
