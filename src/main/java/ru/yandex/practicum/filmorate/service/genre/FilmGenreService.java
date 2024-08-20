package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.Set;

public interface FilmGenreService {
    Collection<FilmGenre> getAll();

    FilmGenre getGenreById(Long genreId);

    Set<FilmGenre> getGenresByFilmId(Long id);
}
