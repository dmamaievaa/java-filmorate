package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreService {
    Collection<Genre> getAll();

    Genre getGenreById(Long genreId);

    Set<Genre> getGenresByFilmId(Long id);
}
