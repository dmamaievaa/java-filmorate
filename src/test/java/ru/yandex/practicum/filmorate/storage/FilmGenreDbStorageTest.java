package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.genre.FilmGenreService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmGenreDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final FilmGenreService genreService;
    private final FilmGenreDbStorage filmGenreDbStorage;

    private Film film;

    @BeforeEach
    void setUp() {
        film = TestUtil.createFilm("Gone with the wind", LocalDate.of(1939, 8, 17), 136, 1, "NC-17");
    }

    @Test
    void shouldGetAllGenres() {
        Collection<FilmGenre> genres = genreService.getAll();
        assertEquals(6, genres.size());
    }

    @Test
    void shouldSetFilmGenre() {
        assertTrue(film.getFilmGenre().isEmpty());

        film.getFilmGenre().add(FilmGenre.builder()
                .id(1L)
                .name("Comedy")
                .build());

        assertEquals(1, film.getFilmGenre().size());
    }

    @Test
    void shouldGetGenreForId() {
        FilmGenre genreTest = genreService.getGenreById(1L);
        assertEquals("Comedy", genreTest.getName());
    }

    @Test
    void shouldAddGenre() {
        assertTrue(film.getFilmGenre().isEmpty());

        filmDbStorage.add(film);
        film.getFilmGenre().add(FilmGenre.builder()
                .id(1L)
                .name("Comedy")
                .build());
        filmGenreDbStorage.addGenresToFilm(film, film.getFilmGenre());

        Set<FilmGenre> genresFromDb = filmGenreDbStorage.getGenresByFilmId(film.getId());
        assertEquals(1, genresFromDb.size());
        assertEquals("Comedy", genresFromDb.iterator().next().getName());
    }

    @Test
    void shouldUpdateGenre() {
        filmDbStorage.add(film);

        filmGenreDbStorage.addGenresToFilm(film, Set.of(
                FilmGenre.builder()
                        .id(1L)
                        .name("Comedy")
                        .build()));

        film.getFilmGenre().clear();
        film.getFilmGenre().add(FilmGenre.builder()
                .id(2L)
                .name("Drama")
                .build());
        filmGenreDbStorage.addGenresToFilm(film, film.getFilmGenre());

        Set<FilmGenre> genresFromDb = filmGenreDbStorage.getGenresByFilmId(film.getId());
        assertEquals(1, genresFromDb.size());
        assertEquals("Drama", genresFromDb.iterator().next().getName());
    }
}