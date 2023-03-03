create table t_system_user
(
    id          varchar(255) not null primary key,
    account     varchar(255),
    name        varchar(255),
    corn        varchar(255),
    template_id varchar(255),
    pet_name    varchar(255),
    greeting    varchar(255),
    city        varchar(255),
    birthday    varchar(255)
);

create table t_memorial_day
(
    id      varchar(255) not null primary key,
    account varchar(255),
    name    varchar(255),
    date    varchar(255)
);