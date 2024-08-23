package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final NamedParameterJdbcOperations jdbc;

    private static final String SQL_MPA_SELECT_ALL = "SELECT * FROM mpa";
    private static final String SQL_MPA_SELECT_BY_ID = "SELECT * FROM mpa WHERE id = :id";
    private static final String SQL_MPA_SELECT_BY_FILM_ID = "SELECT m.id, m.name FROM mpa m " +
            "JOIN films f ON m.id = f.mpa_id WHERE f.id = :filmId";
    private static final String SQL_MPA_INSERT = "INSERT INTO mpa (name) VALUES (:name)";

    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbc.query(SQL_MPA_SELECT_ALL, mpaMapper);
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", mpaId);
        return jdbc.query(SQL_MPA_SELECT_BY_ID, params, mpaMapper)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("MPA with id " + mpaId + " not found"));
    }

    @Override
    public Mpa getMpaByFilmId(Long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return jdbc.query(SQL_MPA_SELECT_BY_FILM_ID, params, mpaMapper)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA for film with id " + filmId + " not found"));
    }

    @Override
    public void addMpa(Film film) {
        int mpaId = film.getMpa().getId();
        Mpa existingMpa = jdbc.query(SQL_MPA_SELECT_BY_ID, new MapSqlParameterSource("id", mpaId), mpaMapper)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA with ID " + mpaId + " not found"));
        film.setMpa(existingMpa);
    }

    private final RowMapper<Mpa> mpaMapper = new RowMapper<>() {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Mpa.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    };
}
