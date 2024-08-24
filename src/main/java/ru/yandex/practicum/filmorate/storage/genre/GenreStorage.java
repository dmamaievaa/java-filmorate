package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAllGenres();

    Optional<Genre> getGenreById(Long genreId);

    HashSet<Genre> getGenresByFilmId(Long id);

    void updateFilmGenre(Long filmId, Long genreId);

    void addGenresToFilm(Film film, Set<Genre> listGenre);

    void deleteAllGenresByFilmId(Long filmId);
}
