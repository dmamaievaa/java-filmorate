package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class FilmGenreController {

    private final GenreService filmGenreService;


    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Long genreId) {
        return filmGenreService.getGenreById(genreId);
    }

    @GetMapping()
    public Collection<Genre> getAllGenres() {
        return filmGenreService.getAll();
    }
}