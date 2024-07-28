package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("Film with id = {} was successfully added", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            log.info("Film with id = {} was successfully updated", film.getId());
        } else {
            throw new NotFoundException("Cannot update film as it does not exist");
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        log.trace("Starting search for film with id = {}", id);
        Film film = films.get(id);
        if (film == null) {
            log.warn("Film with id = {} not found", id);
            throw new NotFoundException(String.format("Film with id = %d not found", id));
        } else {
            log.trace("Film with id = {} found", id);
            return film;
        }
    }
}
