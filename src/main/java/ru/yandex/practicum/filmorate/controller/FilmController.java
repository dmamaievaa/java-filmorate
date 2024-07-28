package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ValidFilm;

import java.util.List;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;
    private final String like = "/{id}/like/{userId}";

    @Autowired
    public FilmController(FilmService filmService,FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @PostMapping
    public Film add(@ValidFilm @RequestBody Film film) {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@ValidFilm @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping(like)
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Adding like to film with id: {} by user with id: {}", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(like)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like from film with id: {} by user with id: {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10",
            required = false) @Positive int count) {
        return filmService.getPopular(count);
    }
}