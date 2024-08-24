package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final NamedParameterJdbcOperations jdbcOperations;

    private static final String SQL_LIKES_SELECT_BY_ID = "SELECT user_id FROM likes WHERE film_id = :filmId";

    @Override
    public Set<Long> getLikesByFilmId(Long id) {
        Set<Long> likes = new HashSet<>();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", id);
        SqlRowSet likeRows = jdbcOperations.queryForRowSet(SQL_LIKES_SELECT_BY_ID, params);
        while (likeRows.next()) {
            likes.add(likeRows.getLong("user_id"));
        }
        return likes;
    }
}