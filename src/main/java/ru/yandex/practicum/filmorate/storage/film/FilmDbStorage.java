package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmGenreStorage filmGenreStorage;
    private final LikesStorage likesStorage;

    private static final String SQL_FILMS_SELECT_ALL = "SELECT f.*, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id";
    private static final String SQL_FILMS_INSERT = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";
    private static final String SQL_FILMS_UPDATE = "UPDATE films SET name = :name, description = :description, " +
            "release_date = :releaseDate, duration = :duration, mpa_id = :mpaId WHERE id = :id";
    private static final String SQL_FILMS_SELECT_BY_ID = "SELECT f.*, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "WHERE f.id = :id";
    private static final String SQL_LIKES_INSERT = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
    private static final String SQL_LIKES_DELETE = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
    private static final String SQL_FILMS_GET_RATING = "SELECT f.*, COUNT(l.user_id) AS count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id " +
            "ORDER BY count DESC " +
            "LIMIT :count";

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbc.query(SQL_FILMS_SELECT_ALL, filmMapper);
        for (Film film : films) {
            Set<FilmGenre> genres = filmGenreStorage.getGenresByFilmId(film.getId());
            film.setFilmGenre(genres);
            Set<Long> likes = likesStorage.getLikesByFilmId(film.getId());
            film.setLikes(likes);
        }
        return films;
    }

    @Override
    public Film add(Film film) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(SQL_FILMS_INSERT, params, keyHolder);
        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());

        int rowsUpdated = jdbc.update(SQL_FILMS_UPDATE, params);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Cannot update film as it does not exist");
        }
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(SQL_LIKES_INSERT, params);
        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(SQL_LIKES_DELETE, params);
        return film;
    }

    @Override
    public List<Film> getRating(int count) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("count", count);
        return jdbc.query(SQL_FILMS_GET_RATING, params, filmMapper);
    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", filmId);

        return jdbc.queryForObject(SQL_FILMS_SELECT_BY_ID, params, filmMapper);
    }

    private final RowMapper<Film> filmMapper = new RowMapper<>() {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getLong("duration"))
                    .mpa(Mpa.builder()
                            .id(rs.getInt("mpa_id"))
                            .name(rs.getString("mpa_name"))
                            .build())
                    .likes(new HashSet<>())
                    .filmGenre(new HashSet<>())
                    .build();
        }
    };
}
