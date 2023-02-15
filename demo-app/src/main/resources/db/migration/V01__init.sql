create table USERS
(
    id    bigserial          not null
        constraint users_pkey primary key,
    name  varchar(255)       not null,
    age   integer,
    email varchar(80) unique not null
);

create table PROFILES
(
    id       bigserial      not null
        constraint profiles_pkey primary key,
    cash     numeric(17, 2) not null , -- maxValue = 999.999.999.999.999.99
    max_cash numeric(17, 2) not null ,
    user_id  integer
);

create table PHONES
(
    id          bigserial   not null
        constraint phones_pkey primary key,
    phone_value varchar(25) not null,
    user_id     integer
);

alter table PROFILES
    add constraint fk_users_profiles
        foreign key (user_id) references USERS (id);

alter table PHONES
    add constraint fk_users_phones
        foreign key (user_id) references USERS (id);

-- insert into USERS (id, name, age, email)
-- values (1, 'user1', 42, 'user1@mail.ru'),
--        (2, 'user2', 42, 'user2@mail.ru'),
--        (3, 'user3', 42, 'user3@mail.ru'),
--        (4, 'user4', 42, 'user4@mail.ru');
--
-- insert into PROFILES (id, cash, max_cash, user_id)
-- values (1, 1000, 2070, 1),
--        (2, 1000, 2070, 2),
--        (3, 1000, 2070, 3),
--        (4, 1000, 2070, 4);
--
--
-- insert into PHONES (id, phone_value, user_id)
-- values (1, '+79998887701', 1),
--        (2, '+12223334401', 1),
--        (3, '+12223334402', 2),
--        (4, '+79998887702', 2),
--        (5, '+79998887703', 3);


