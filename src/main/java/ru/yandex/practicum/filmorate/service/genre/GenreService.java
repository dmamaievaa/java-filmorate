package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getAll();

    Genre getGenreById(Long genreId);
}
