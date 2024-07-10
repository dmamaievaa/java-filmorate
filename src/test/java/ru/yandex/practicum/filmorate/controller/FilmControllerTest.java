package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {

    private FilmController filmController;
    private Validator validator;
    private FilmValidator filmValidator;


    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmValidator = new FilmValidator();
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
        assertFalse(violations.isEmpty());
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
}