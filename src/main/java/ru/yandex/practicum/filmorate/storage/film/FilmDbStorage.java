package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final GenreDbStorage filmGenreStorage;
    private final LikesStorage likesStorage;
    private final MpaDbStorage mpaDbStorage;

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
            LinkedHashSet<Genre> genres = filmGenreStorage.getGenresByFilmId(film.getId());
            film.setGenres(genres);
            Set<Long> likes = likesStorage.getLikesByFilmId(film.getId());
            film.setLikes(likes);
        }
        return films;
    }

    @Override
    public Film add(Film film) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info("Starting the addition of a new film: {}", film);

        log.info("Checking existence of MPA with ID: {}", film.getMpa().getId());
        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaDbStorage.getMpaById(film.getMpa().getId()));
        if (mpaOptional.isEmpty()) {
            log.warn("MPA with ID {} not found in the database", film.getMpa().getId());
            throw new ValidationException(String.format("MPA with ID %d does not exist", film.getMpa().getId()));
        }
        Mpa mpa = mpaOptional.orElse(null);
        film.setMpa(mpa);

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            log.warn("Attempt to add a film without specifying genres: {}", film);
            film.setGenres(new LinkedHashSet<>());
        } else {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            for (Genre genre : film.getGenres()) {
                Optional<Genre> genreOptional = filmGenreStorage.getGenreById(genre.getId());
                if (genreOptional.isEmpty()) {
                    log.warn("Genre with ID {} not found in the database", genre.getId());
                    throw new ValidationException(String.format("Genre with ID %d does not exist", genre.getId()));
                }
                genres.add(genreOptional.get());
            }
            film.setGenres(genres);
        }

        log.info("Inserting the film into the database: {}", film);
        SqlParameterSource filmParams = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(SQL_FILMS_INSERT, filmParams, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        log.info("Film successfully added with ID: {}", filmId);

        film.setLikes(new HashSet<>());
        log.info("Film added successfully: {}", film);
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
        //filmGenreStorage.addGenresToFilm(film, (LinkedHashSet<Genre>) film.getGenres());
        filmGenreStorage.addGenresToFilm(film, film.getGenres());

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

        Film film = jdbc.queryForObject(SQL_FILMS_SELECT_BY_ID, params, filmMapper);

        assert film != null;
        filmGenreStorage.load(List.of(film));

        Set<Long> likes = likesStorage.getLikesByFilmId(filmId);
        film.setLikes(likes);

        return film;
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
                    .genres(new LinkedHashSet<>())
                    .build();
        }
    };
}
