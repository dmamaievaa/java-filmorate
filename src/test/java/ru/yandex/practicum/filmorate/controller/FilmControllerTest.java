package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Validator validator;
    private FilmValidator filmValidator;
    private InMemoryFilmStorage filmStorage;
    private FilmService filmService;
    private UserService userService;
    private InMemoryUserStorage userStorage;


    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmValidator = new FilmValidator();
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService =  new UserServiceImpl(userStorage);
        filmService = new FilmServiceImpl(filmStorage, userService);
        filmController = new FilmController(filmService);
    }

    @Test
    void testAddValidFilm() {
        Film validFilm = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film addedFilmSuccessfully = filmController.add(validFilm);
        assertEquals(1, addedFilmSuccessfully.getId());
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAddInvalidFilm() {
        Film invalidFilm = Film.builder()
                .name("")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film addedFilmFailed = filmController.add(invalidFilm);
        assertEquals(1, addedFilmFailed.getId());
        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        assertFalse(violations.isEmpty());    }

    @Test
    void testUpdateFilm() {
        Film existingFilm = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film updatedFilm = Film.builder()
                .id(1)
                .name("Updated Film")
                .description("Updated description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .build();

        filmController.add(existingFilm);

        Film result = filmController.update(updatedFilm);
        assertEquals("Updated Film", result.getName());
        assertEquals(LocalDate.of(2021, 1, 1), result.getReleaseDate());
    }

    @Test
    void testGetAllFilms() {
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .build();

        filmController.add(film1);
        filmController.add(film2);

        List<Film> allFilms = filmController.getAll();
        assertEquals(2, allFilms.size());
    }

    @Test
    void testInvalidReleaseFilmDate() {
        Film invalidFilm = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(1700, 1, 1))
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> {
            filmValidator.isValid(invalidFilm, null);
        });
    }

    @Test
    void testAddLike() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        filmController.add(film);
        User user = new User(1, "user@example.com", "user", "User", LocalDate.of(1990, 1, 1));
        userService.add(user);

        filmController.addLike(film.getId(), user.getId());

        Film likedFilm = filmController.getFilmById(film.getId());
        assertEquals(1, likedFilm.getLikesCount());
    }

    @Test
    void testRemoveLike() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        filmController.add(film);
        User user = new User(1, "user@example.com", "user", "User", LocalDate.of(1990, 1, 1));
        userService.add(user);

        filmController.addLike(film.getId(), user.getId());
        filmController.removeLike(film.getId(), user.getId());

        Film likedFilm = filmController.getFilmById(film.getId());
        assertEquals(0, likedFilm.getLikesCount());
    }

    @Test
    void testGetPopularFilms() {
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .build();

        filmController.add(film1);
        filmController.add(film2);

        User user = new User(1, "user@example.com", "user", "User", LocalDate.of(1990, 1, 1));
        userService.add(user);

        filmController.addLike(film1.getId(), user.getId());

        List<Film> popularFilms = filmController.getPopular(1);
        assertEquals(1, popularFilms.size());
        assertEquals(film1.getId(), popularFilms.get(0).getId());
    }
}