package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ValidFilm;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;
    private FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @PostMapping
    public Film add(@ValidFilm @RequestBody Film film) {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@ValidFilm @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @PutMapping("/{id}/like/{user-id}")
    public void addLike(@PathVariable Long id, @PathVariable("user-id") Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{user-id}")
    public void removeLike(@PathVariable Long id, @PathVariable("user-id") Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10", required = false)
                                            Long count) {
        return filmService.getPopular(count);
    }
}
