package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {

    private static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmId = 1;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Film with id " + film.getId() + " successfully added");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film with id " + film.getId() + " successfully updated");
        } else {
            log.warn("Film with id " + film.getId() + " not found for update");
            throw new ValidationException("No film with such id");
        }
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Film name mustn't be empty or consist of spaces");
            throw new ValidationException("Film name mustn't be empty or contains spaces");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Film description length mustn't exceed 200 characters");
            throw new ValidationException("Film description length mustn't exceed 200 characters");
        } else if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.warn("Incorrect release date");
            throw new ValidationException("Incorrect release date");
        } else if (film.getDuration() <= 0) {
            log.warn("Film duration must be positive");
            throw new ValidationException("Film duration must be positive");
        }
    }
}