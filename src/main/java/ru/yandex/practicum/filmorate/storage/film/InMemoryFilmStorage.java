package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long filmId = 1L;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        film.setId(filmId);
        films.put(filmId, film);
        filmId += 1;
        log.debug("Film with id = {} was successfully added", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            log.debug("Film with id = {} was successfully updated", film.getId());
        } else {
            throw new NotFoundException("Cannot update film as it does not exist");
        }
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        getFilmById(filmId).getLikes().add(userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        if (getFilmById(filmId).getLikes().contains(userId)) {
            getFilmById(filmId).getLikes().remove(userId);
        } else {
            throw new NotFoundException("User did not rated this film.");
        }
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getRating(int count) {
        return getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }


    @Override
    public Film getFilmById(Long id) {
        log.debug("Starting search for film with id = {}", id);
        Film film = films.get(id);
        if (film == null) {
            log.warn("Film with id = {} not found", id);
            throw new NotFoundException(String.format("Film with id = %d not found", id));
        } else {
            log.debug("Film with id = {} found", id);
            return film;
        }
    }
}
