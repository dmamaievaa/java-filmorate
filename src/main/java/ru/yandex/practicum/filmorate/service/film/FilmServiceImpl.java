package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

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
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film add(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        Film film = this.getFilmById(filmId);
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
        filmStorage.update(film);
        film.getLikes().remove(userId);
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