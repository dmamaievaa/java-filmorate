package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmGenreStorage {
    Collection<FilmGenre> getAllGenres();

    Optional<FilmGenre> getGenreById(Long genreId);

    HashSet<FilmGenre> getGenresByFilmId(Long id);

    void updateFilmGenre(Long filmId, Long genreId);

    void addGenresToFilm(Film film, Set<FilmGenre> listGenre);

    void load(List<Film> films);

    void deleteAllGenresByFilmId(Long filmId);
}
