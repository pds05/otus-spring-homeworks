create table authors (
    id        bigserial,
    full_name varchar(255),
    primary key (id)
);

create table genres (
    id   bigserial,
    name varchar(255),
    primary key (id)
);

create table books (
    id        bigserial,
    title     varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);


create table books_genres (
    book_id  bigint references books (id) on delete cascade,
    genre_id bigint references genres (id) on delete cascade,
    primary key (book_id, genre_id)
);

create table user_comments (
    id      bigserial,
    text    varchar(255),
    book_id bigint references books (id) on delete cascade,
    primary key (id)
);

create table users (
    username varchar(50) not null unique primary key,
    password varchar(50) not null
);

create table user_roles (
    username varchar(50) not null,
    user_role varchar(50) not null,
    constraint fk_user_roles_users foreign key(username) references users(username) on delete cascade,
    constraint pk_username_user_role primary key (username, user_role)
);