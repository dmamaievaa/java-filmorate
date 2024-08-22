package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class TestUtil {

    public static Film createFilm(String name, LocalDate releaseDate, int duration, int mpaId, String mpaName) {
        Film film = Film.builder()
                .name(name)
                .description("Description of " + name)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(Mpa.builder()
                        .id(mpaId)
                        .name(mpaName)
                        .build())
                .build();
        film.setGenres(new LinkedHashSet<>());
        film.setLikes(new HashSet<>());
        return film;
    }

    public static Film createFilmWithId(Long id, String name, LocalDate releaseDate, int duration, int mpaId, String mpaName) {
        Film film = createFilm(name, releaseDate, duration, mpaId, mpaName);
        film.setId(id);
        return film;
    }

    public static User createUser(String email, String login, LocalDate birthday) {
        User user = User.builder()
                .email(email)
                .login(login)
                .birthday(birthday)
                .build();
        user.setFriends(new HashSet<>());
        return user;
    }

    public static User createUserWithId(Long id, String email, String login, LocalDate birthday) {
        User user = createUser(email, login, birthday);
        user.setId(id);
        return user;
    }

    public static Genre createGenre(Long id, String name) {
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Mpa createMpa(int id, String name) {
        return Mpa.builder()
                .id(id)
                .name(name)
                .build();
    }
}
