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

create table users (
    id       bigserial,
    user_name varchar(255),
    primary key (id)
);

create table user_comments (
    id      bigserial,
    text    varchar(255),
    user_id bigint references users(id) on delete cascade,
    book_id bigint references books(id) on delete cascade,
    primary key (user_id, book_id)
);