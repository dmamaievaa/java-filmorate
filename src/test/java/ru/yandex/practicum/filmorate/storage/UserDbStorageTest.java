package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private User user;
    private User friend;
    private User commonFriend;

    @BeforeEach
    void setUp() {
        namedParameterJdbcOperations.update("DELETE FROM friends", new MapSqlParameterSource());
        namedParameterJdbcOperations.update("DELETE FROM users", new MapSqlParameterSource());

        user = User.builder()
                .email("user@example.com")
                .login("user")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        user.setFriends(new HashSet<>());

        friend = User.builder()
                .email("user1@example.com")
                .login("user1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        friend.setFriends(new HashSet<>());

        commonFriend = User.builder()
                .email("user2@example.com")
                .login("user2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        commonFriend.setFriends(new HashSet<>());
    }

    @Test
    void shouldCreateAndGetUser() {
        userDbStorage.add(user);

        assertEquals(user, userDbStorage.getUserById(user.getId()));
        assertEquals(user.getLogin(), userDbStorage.getUserById(user.getId()).getName());
        assertEquals(1, userDbStorage.getAll().size());
    }

    @Test
    void shouldUpdateAndGetUser() {
        userDbStorage.add(user);

        user.setEmail("updated@example.com");
        userDbStorage.update(user);

        assertEquals(user, userDbStorage.getUserById(user.getId()));
        assertEquals(1, userDbStorage.getAll().size());
    }

    @Test
    void shouldCreateAndAddFriend() {
        userDbStorage.add(user);
        userDbStorage.add(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());

        assertEquals(1, userDbStorage.getFriendsByUserId(user.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(friend.getId()).size());
    }

    @Test
    void shouldCreateAndDeleteFriend() {
        userDbStorage.add(user);
        userDbStorage.add(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());

        userDbStorage.deleteFriend(user.getId(), friend.getId());

        assertEquals(0, userDbStorage.getFriendsByUserId(user.getId()).size());
        assertEquals(0, userDbStorage.getFriendsByUserId(friend.getId()).size());
    }

    @Test
    void shouldGetCommonFriends() {
        userDbStorage.add(user);
        userDbStorage.add(friend);
        userDbStorage.add(commonFriend);
        userDbStorage.addFriend(user.getId(), commonFriend.getId());
        userDbStorage.addFriend(friend.getId(), commonFriend.getId());

        assertEquals(commonFriend.getId(), userDbStorage.getCommonFriends(user.getId(), friend.getId()).getFirst().getId());
    }

    @Test
    void shouldThrowExceptionWhenGetFriendWithUnknownId() {
        userDbStorage.add(user);

        Long unknownFriendId = -1L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userDbStorage.getFriendsByUserId(unknownFriendId),
                "Expected getFriendsByUserId to throw, but it didn't"
        );
        assertTrue(exception.getMessage().contains("User not found"));
    }
}