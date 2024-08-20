package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final NamedParameterJdbcOperations jdbc;

    private static final String SQL_GENRES_SELECT_ALL = "SELECT * FROM film_genre";
    private static final String SQL_GENRES_SELECT_BY_ID = "SELECT * FROM film_genre WHERE id = :id";
    private static final String SQL_GENRES_SELECT_BY_FILM_ID =
            "SELECT g.id, g.name " +
                    "FROM film_genre g " +
                    "JOIN genres fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = :filmId";
    private static final String SQL_GENRES_INSERT = "INSERT INTO genres (film_id, genre_id) VALUES (:filmId, :genreId)";
    private static final String SQL_GENRES_DELETE_BY_FILM_ID = "DELETE FROM genres WHERE film_id = :filmId";

    @Override
    public Collection<FilmGenre> getAllGenres() {
        return jdbc.query(SQL_GENRES_SELECT_ALL, genreMapper);
    }

    @Override
    public Optional<FilmGenre> getGenreById(Long genreId) {
        SqlParameterSource params = new MapSqlParameterSource("id", genreId);
        return jdbc.query(SQL_GENRES_SELECT_BY_ID, params, genreMapper).stream().findFirst();
    }


    @Override
    public HashSet<FilmGenre> getGenresByFilmId(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        List<FilmGenre> genreList = jdbc.query(SQL_GENRES_SELECT_BY_FILM_ID, params, genreMapper);
        return new HashSet<>(genreList);
    }

    @Override
    public void updateFilmGenre(Long filmId, Long genreId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("genreId", genreId);
        jdbc.update(SQL_GENRES_INSERT, params);
    }

    @Override
    public void addGenresToFilm(Film film, Set<FilmGenre> listGenre) {
        deleteAllGenresByFilmId(film.getId());
        for (FilmGenre genre : listGenre) {
            updateFilmGenre(film.getId(), genre.getId());
        }
    }

    @Override
    public void load(List<Film> films) {
        for (Film film : films) {
            HashSet<FilmGenre> genres = getGenresByFilmId(film.getId());
            film.setFilmGenre(genres);
        }
    }

    @Override
    public void deleteAllGenresByFilmId(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        jdbc.update(SQL_GENRES_DELETE_BY_FILM_ID, params);
    }

    private final RowMapper<FilmGenre> genreMapper = new RowMapper<>() {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return FilmGenre.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    };
}
