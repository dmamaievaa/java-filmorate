package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private UserServiceImpl userService;
    private UserValidator userValidator;
    private User validUser;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        userService = new UserServiceImpl(userStorage);
        userController = new UserController(userService);
        userValidator = new UserValidator();
        validUser = TestUtil.createValidUser();
        user1 = TestUtil.createFirstUser();
        user2 = TestUtil.createSecondUser();
        user3 = TestUtil.createThirdUser();
    }

    @Test
    void shouldAddUser() {
        User addedUser = userController.add(validUser);
        assertEquals(1L, addedUser.getId());
        userValidator.isValid(addedUser, null);
    }

    @Test
    void shouldUpdateUser() {
        userController.add(validUser);

        User updatedUser = new User(
                validUser.getId(),
                "updateduser@example.com",
                "updateduserlogin",
                "updatedusername",
                LocalDate.of(2001, 1, 1),
                new HashSet<>()
        );

        User result = userController.update(updatedUser);
        assertEquals("updateduser@example.com", result.getEmail());
        assertEquals("updateduserlogin", result.getLogin());
    }

    @Test
    void shouldAddFriend() {
        userController.add(user1);
        userController.add(user2);
        userController.addFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2));
    }

    @Test
    void shouldRemoveFriend() {
        userController.add(user1);
        userController.add(user2);
        userController.addFriend(user1.getId(), user2.getId());
        userController.deleteFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertEquals(0, friends.size());
    }

    @Test
    void shoulReturnCommonFriends() {
        userController.add(user1);
        userController.add(user2);
        userController.add(user3);
        userController.addFriend(user1.getId(), user3.getId());
        userController.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user3));
    }

    @Test
    void shouldThrowErrorOnInvalidUserBirthday() {
        User invalidUserBirthday = new User(
                0L,
                "user@example.com",
                "user2login",
                "Name",
                LocalDate.of(2025, 1, 1),
                new HashSet<>()
        );

        assertThrows(ValidationException.class, () -> userValidator.isValid(invalidUserBirthday, null));
    }

    @Test
    void shouldSetAutomaticUserName() {
        User userWithoutName = new User(
                0L,
                "user@example.com",
                "userlogin",
                null,
                LocalDate.of(2000, 1, 1),
                new HashSet<>()
        );

        userValidator.isValid(userWithoutName, null);

        assertEquals("userlogin", userWithoutName.getName());
    }
}