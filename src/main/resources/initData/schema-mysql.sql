drop table if exists users;

create table if not exists users (
    id serial not null,
    user_id varchar(20) not null,
    user_nm varchar(20) not null,
    password varchar(256) not null,
    email varchar(200) not null,
    user_role varchar(50) not null,
    crt_id varchar(20),
    crt_dt timestamp,
    mdf_id varchar(20),
    mdf_dt timestamp,
    constraint pk_users primary key (user_id)
);
