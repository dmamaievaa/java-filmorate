package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcOperations jdbc;

    private static final String SQL_USERS_SELECT_ALL = "SELECT * FROM users";
    private static final String SQL_USERS_UPDATE = "UPDATE users SET email = :email, login = :login, " +
            "name = :name, birthday = :birthday WHERE id = :id";
    private static final String SQL_USERS_SELECT_BY_ID = "SELECT * FROM users WHERE id = :id";
    private static final String SQL_ADD_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (:userId, :friendId)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId";
    private static final String SQL_GET_FRIENDS_BY_USER_ID = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
            "FROM users u " +
            "JOIN friends f ON u.id = f.friend_id " +
            "WHERE f.user_id = :userId";
    private static final String SQL_GET_COMMON_FRIENDS = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
            "FROM users u " +
            "JOIN friends f1 ON u.id = f1.friend_id " +
            "JOIN friends f2 ON u.id = f2.friend_id " +
            "WHERE f1.user_id = :userId AND f2.user_id = :friendId";

    @Override
    public Collection<User> getAll() {
        return jdbc.query(SQL_USERS_SELECT_ALL, userRowMapper);
    }

  public User add(User user) {
     String sql = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";

     MapSqlParameterSource params = new MapSqlParameterSource()
             .addValue("email", user.getEmail())
             .addValue("login", user.getLogin())
             .addValue("name", user.getName())
             .addValue("birthday", user.getBirthday());

     KeyHolder keyHolder = new GeneratedKeyHolder();
     jdbc.update(sql, params, keyHolder, new String[] {"id"});

     Long id = keyHolder.getKey().longValue();
     user.setId(id);

     return user;
 }


    @Override
    public User update(User user) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        int rowsUpdated = jdbc.update(SQL_USERS_UPDATE, params);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Cannot update user as it does not exist");
        }
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userId);

        List<User> users = jdbc.query(SQL_USERS_SELECT_BY_ID, params, userRowMapper);
        if (users.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        return users.getFirst();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);

        jdbc.update(SQL_ADD_FRIEND, params);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        log.debug("Deleting friend with ID {} from user with ID {}", friendId, userId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);
        jdbc.update(SQL_DELETE_FRIEND, params);
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        if (!userExists(id)) {
            throw new NotFoundException(String.format("User not found ID %d", id));
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", id);
        return jdbc.query(SQL_GET_FRIENDS_BY_USER_ID, params, userRowMapper);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);

        return jdbc.query(SQL_GET_COMMON_FRIENDS, params, userRowMapper);
    }

    private void validateUsersExist(Long userId, Long friendId) {
        if (!userExists(userId) || !userExists(friendId)) {
            throw new NotFoundException("User or friend not found.");
        }
    }

    private boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
        Integer count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }


    private final RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getLong("id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();
        }
    };
}