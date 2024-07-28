package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.time.LocalDate;
import java.util.Collection;


import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private FilmValidator filmValidator;
    private InMemoryFilmStorage filmStorage;
    private FilmServiceImpl filmService;
    private UserServiceImpl userService;
    private InMemoryUserStorage userStorage;
    private User user1;

    @BeforeEach
    void setUp() {
        filmValidator = new FilmValidator();
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserServiceImpl(userStorage);
        filmService = new FilmServiceImpl();
        filmController = new FilmController(filmService, filmStorage);
        user1 = TestUtil.createFirstUser();
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
    }

    @Test
    void testUpdateFilm() {
        Film existingFilm = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film addedFilm = filmController.add(existingFilm);

        Film updatedFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Updated Film")
                .description("Updated description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .build();

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

        Collection<Film> allFilms = filmController.getAll();
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

        Film addedFilm = filmController.add(film);
        userService.add(user1);

        filmController.addLike(addedFilm.getId(), user1.getId());

        Film likedFilm = filmController.getAll().stream().findFirst().orElse(null);
        assertNotNull(likedFilm);
        assertEquals(1, likedFilm.getLikes().size());
    }

    @Test
    void testRemoveLike() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film addedFilm = filmController.add(film);
        userService.add(user1);
        filmController.addLike(addedFilm.getId(), user1.getId());
        filmController.removeLike(addedFilm.getId(), user1.getId());

        Film likedFilm = filmController.getAll().stream().findFirst().orElse(null);
        assertNotNull(likedFilm);
        assertEquals(0, likedFilm.getLikes().size());
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

        userService.add(user1);
        filmController.addLike(film1.getId(), user1.getId());

        Collection<Film> popularFilms = filmController.getPopular(1L);
        assertEquals(1, popularFilms.size());
        assertEquals(film1.getId(), popularFilms.iterator().next().getId());
    }

    @Test
    void testUserCannotLikeFilmTwice() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film addedFilm = filmController.add(film);
        userService.add(user1);
        filmController.addLike(addedFilm.getId(), user1.getId());
        filmController.addLike(addedFilm.getId(), user1.getId());

        Film likedFilm = filmController.getAll().stream().findFirst().orElse(null);
        assertNotNull(likedFilm);
        assertEquals(1, likedFilm.getLikes().size());
    }
}