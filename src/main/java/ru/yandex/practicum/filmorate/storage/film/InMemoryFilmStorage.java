package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        film.setId(filmId);
        films.put(film.getId(), film);
        filmId += 1;
        log.info("Film with id = {} was successfully added", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        int idFilm = film.getId();
        if (films.containsKey(idFilm)) {
            films.put(idFilm, film);
            log.info("Film with id = {} was successfully updated", film.getId());
        } else {
            throw new NotFoundException("Cannot update film as it does not exist");
        }
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }
}
