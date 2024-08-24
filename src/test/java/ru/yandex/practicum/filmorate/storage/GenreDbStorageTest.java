package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;
    private final GenreDbStorage filmGenreDbStorage;

    private Film film;

    @BeforeEach
    void setUp() {
        film = TestUtil.createFilm("Gone with the wind", LocalDate.of(1939, 8, 17), 136, 1, "NC-17");
    }

    @Test
    void shouldGetAllGenres() {
        Collection<Genre> genres = genreService.getAll();
        assertEquals(6, genres.size());
    }

    @Test
    void shouldSetFilmGenre() {
        assertTrue(film.getGenres().isEmpty());

        film.getGenres().add(Genre.builder()
                .id(1L)
                .name("Комедия")
                .build());

        assertEquals(1, film.getGenres().size());
    }

    @Test
    void shouldGetGenreForId() {
        Genre genreTest = genreService.getGenreById(1L);
        assertEquals("Комедия", genreTest.getName());
    }

    @Test
    void shouldAddGenre() {
        assertTrue(film.getGenres().isEmpty());

        filmDbStorage.add(film);
        film.getGenres().add(Genre.builder()
                .id(1L)
                .name("Комедия")
                .build());
        filmGenreDbStorage.addGenresToFilm(film, film.getGenres());

        LinkedHashSet<Genre> genresFromDb = (LinkedHashSet<Genre>) filmGenreDbStorage.getGenresByFilmId(film.getId());
        assertEquals(1, genresFromDb.size());
        assertEquals("Комедия", genresFromDb.iterator().next().getName());
    }

    @Test
    void shouldUpdateGenre() {
        filmDbStorage.add(film);

        LinkedHashSet<Genre> initialGenres = new LinkedHashSet<>();
        initialGenres.add(Genre.builder()
                .id(1L)
                .name("Комедия")
                .build());
        filmGenreDbStorage.addGenresToFilm(film, initialGenres);

        film.getGenres().clear();
        film.getGenres().add(Genre.builder()
                .id(2L)
                .name("Драма")
                .build());
        filmGenreDbStorage.addGenresToFilm(film, film.getGenres());

        Set<Genre> genresFromDb = filmGenreDbStorage.getGenresByFilmId(film.getId());
        assertEquals(1, genresFromDb.size());
        assertEquals("Драма", genresFromDb.iterator().next().getName());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidGenreId() {
        film.getGenres().add(Genre.builder()
                .id(500L)
                .build());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmDbStorage.add(film);
            filmGenreDbStorage.addGenresToFilm(film, (LinkedHashSet<Genre>) film.getGenres());
        });

        assertEquals("Genre with ID 500 does not exist", exception.getMessage());
    }
}