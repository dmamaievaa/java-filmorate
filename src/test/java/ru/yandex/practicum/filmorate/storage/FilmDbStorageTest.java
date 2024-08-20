package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserDbStorage userDbStorage;
    private final LikesDbStorage likeDbStorage;
    private Film film;
    private Film film2;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        film = TestUtil.createFilm("name", LocalDate.of(1999, 8, 17), 136, 1, "G");
        film2 = TestUtil.createFilm("name2", LocalDate.of(1999, 8, 17), 136, 1, "G");
        user = TestUtil.createUser("user@example.com", "user", LocalDate.of(1990, 1, 1));
        user2 = TestUtil.createUser("user1@example.com", "user1", LocalDate.of(1995, 11, 15));
    }


    @Test
    void addFilmTest() {
        filmDbStorage.add(film);

        assertEquals(film, filmDbStorage.getFilmById(film.getId()));
    }

    @Test
    void updateFilmTest() {
        filmDbStorage.add(film);
        assertEquals(film, filmDbStorage.getFilmById(film.getId()));

        film.setName("updateName");
        filmDbStorage.update(film);
        assertEquals("updateName", filmDbStorage.getFilmById(film.getId()).getName());
    }

    @Test
    void likeAndDeleteLikeTest() {
        filmDbStorage.add(film);
        userDbStorage.add(user);
        userDbStorage.add(user2);
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.addLike(film.getId(), user2.getId());
        film.setLikes(likeDbStorage.getLikesByFilmId(film.getId()));
        assertEquals(2, film.getLikes().size());

        filmDbStorage.deleteLike(film.getId(), user.getId());
        film.setLikes(likeDbStorage.getLikesByFilmId(film.getId()));
        assertEquals(1, film.getLikes().size());
    }

    @Test
    void getRatingTest() {
        filmDbStorage.add(film);
        userDbStorage.add(user);
        userDbStorage.add(user2);
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.addLike(film.getId(), user2.getId());

        Long filmId = film.getId();
        List<Film> popularFilms = filmService.getPopular(1);

        assertEquals(filmId, popularFilms.getFirst().getId());
    }

}
