package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film getFilmById(int filmId) {
        return filmStorage
                .getFilmById(filmId)
                .orElseThrow(() -> {
                    String errorMessage = "Film with id = " + filmId + " not found in the database";
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public Film addLike(int filmId, int userId) {
        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);
        if (filmOptional.isEmpty()) {
            log.error("Film with id = {} not found in the database", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with id = " + filmId + " not found in the database");
        }

        Optional<User> userOptional = userStorage.getUserById(userId);
        if (userOptional.isEmpty()) {
            log.error("User with id = {} not found in the database", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " not found in the database");
        }

        Film film = filmOptional.get();

        if (film.getLikes().contains(userId)) {
            log.info("User id = {} already liked film with id = {}", userId, filmId);
            return film;
        }

        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Like to film with id = {} successfully added", filmId);
        return film;
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            log.info("Like to film with id = {}, from user id = {} does not exist", filmId, userId);
            throw new NotFoundException("Like from user id = " + userId + " not found.");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Like to film with id = {} successfully removed", filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        Collection<Film> allFilms = filmStorage.getAll();
        return allFilms.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}