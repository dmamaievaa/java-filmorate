package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDbStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmGenreServiceImpl implements FilmGenreService {

    @Qualifier("filmGenreDbStorage")
    private final FilmGenreDbStorage filmGenreStorage;


    @Override
    public Collection<FilmGenre> getAll() {
        return filmGenreStorage.getAllGenres();
    }

    @Override
    public FilmGenre getGenreById(Long genreId) {
        return filmGenreStorage
                .getGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre with id = " + genreId + " is not in the database"));
    }

    @Override
    public Set<FilmGenre> getGenresByFilmId(Long id) {
        return new HashSet<>(filmGenreStorage.getGenresByFilmId(id));
    }
}
