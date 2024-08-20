DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;


CREATE TABLE IF NOT EXISTS mpa
(
    id   BIGINT primary key auto_increment,
    name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    id           BIGINT primary key auto_increment,
    name         varchar      NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date         NOT NULL,
    duration     int          NOT NULL,
    mpa_id       bigint       NOT NULL REFERENCES mpa (id)
    );

CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT primary key auto_increment,
    email    varchar     NOT NULL,
    login    varchar     NOT NULL,
    name     varchar     NULL,
    birthday date        NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    id   BIGINT primary key auto_increment,
    name varchar  NOT NULL
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id       bigint  NOT NULL REFERENCES users (id),
    friend_id     bigint  NOT NULL REFERENCES users (id),
    friendship    boolean NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    film_id  bigint NOT NULL REFERENCES films (id),
    genre_id bigint NOT NULL REFERENCES film_genre (id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id bigint NOT NULL REFERENCES users (id),
    film_id bigint NOT NULL REFERENCES films (id),
    PRIMARY KEY (user_id, film_id)
);