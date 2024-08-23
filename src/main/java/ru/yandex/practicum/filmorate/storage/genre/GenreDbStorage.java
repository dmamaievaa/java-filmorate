package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcOperations jdbc;

    private static final String SQL_GENRES_SELECT_ALL = "SELECT * FROM genres";
    private static final String SQL_GENRES_SELECT_BY_ID = "SELECT * FROM genres WHERE id = :id";
    private static final String SQL_GENRES_SELECT_BY_FILM_ID =
            "SELECT g.id, g.name " +
                    "FROM genres g " +
                    "JOIN film_genre fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = :filmId";
    private static final String SQL_GENRES_INSERT = "INSERT INTO film_genre (film_id, genre_id) VALUES (:filmId, :genreId)";
    private static final String SQL_GENRES_DELETE_BY_FILM_ID = "DELETE FROM film_genre WHERE film_id = :filmId";

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbc.query(SQL_GENRES_SELECT_ALL, genreMapper);
    }

    @Override
    public Optional<Genre> getGenreById(Long genreId) {
        SqlParameterSource params = new MapSqlParameterSource("id", genreId);
        return jdbc.query(SQL_GENRES_SELECT_BY_ID, params, genreMapper).stream().findFirst();
    }

    @Override
    public LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        List<Genre> genreList = jdbc.query(SQL_GENRES_SELECT_BY_FILM_ID, params, genreMapper);
        return new LinkedHashSet<>(genreList);
    }

    @Override
    public void updateFilmGenre(Long filmId, Long genreId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("genreId", genreId);
        jdbc.update(SQL_GENRES_INSERT, params);
    }

    @Override
    public void addGenresToFilm(Film film, LinkedHashSet<Genre> listGenre) {
        if (listGenre == null) {
            listGenre = new LinkedHashSet<>();
        }

        deleteAllGenresByFilmId(film.getId());
        for (Genre genre : listGenre) {
            if (getGenreById(genre.getId()).isEmpty()) {
                throw new ValidationException("Genre with ID " + genre.getId() + " not found");
            }
            updateFilmGenre(film.getId(), genre.getId());
        }
    }

    @Override
    public void deleteAllGenresByFilmId(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        jdbc.update(SQL_GENRES_DELETE_BY_FILM_ID, params);
    }

    private final RowMapper<Genre> genreMapper = new RowMapper<>() {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Genre.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    };
}