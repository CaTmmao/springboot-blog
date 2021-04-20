
create table user
(
    id         int auto_increment
        primary key,
    username   varchar(100) unique,
    pwd        varchar(100),
    created_at datetime,
    updated_at datetime,
    avatar     varchar(100)
);
