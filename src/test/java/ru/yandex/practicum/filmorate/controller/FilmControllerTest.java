package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void testAddFilm() {
        Film validFilm = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film invalidFilm = Film.builder()
                .name(null)
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.add(invalidFilm));
        Film addedFilm = filmController.add(validFilm);
        assertEquals(1, addedFilm.getId());
    }

    @Test
    void testUpdateFilm() {
        Film existingFilm = Film.builder()
                .id(1)
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
                .id(1)
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        Film film2 = Film.builder()
                .id(2)
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
    void shouldThrowExceptionWhenNameIsNull() {
        Film film = Film.builder()
                .name(null)
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description".repeat(13))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(1890, 1, 1))
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.add(film));
    }
}