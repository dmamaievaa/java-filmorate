package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.validation.ValidFilm;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        return filmService.getById(id);
    }


    @PostMapping
    public Film add(@ValidFilm @RequestBody Film film) {
        log.info("Received request to add a new film: {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@ValidFilm @RequestBody Film newFilm) {
        return filmService.update(newFilm);
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
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10",
            required = false) int count) {
        return filmService.getPopular(count);
    }
}
