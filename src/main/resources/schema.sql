DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa;

CREATE TABLE IF NOT EXISTS mpa (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR(200) NOT NULL,
                                     release_date DATE NOT NULL,
                                     duration INT NOT NULL,
                                     mpa_id BIGINT,
                                     FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     email VARCHAR(255) NOT NULL,
                                     login VARCHAR(255) NOT NULL,
                                     name VARCHAR(255),
                                     birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
                                       user_id BIGINT,
                                       friend_id BIGINT,
                                       friendship BOOLEAN NOT NULL DEFAULT FALSE,
                                       PRIMARY KEY (user_id, friend_id),
                                       FOREIGN KEY (user_id) REFERENCES users(id),
                                       FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS film_genre (
                                          film_id BIGINT NOT NULL,
                                          genre_id BIGINT NOT NULL,
                                          PRIMARY KEY (film_id, genre_id),
                                          FOREIGN KEY (film_id) REFERENCES films(id),
                                          FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS likes (
                                     user_id BIGINT NOT NULL,
                                     film_id BIGINT NOT NULL,
                                     PRIMARY KEY (user_id, film_id),
                                     FOREIGN KEY (user_id) REFERENCES users(id),
                                     FOREIGN KEY (film_id) REFERENCES films(id)
);