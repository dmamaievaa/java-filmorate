package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        userService = new UserServiceImpl(new InMemoryUserStorage());
        userController = new UserController(userService, new InMemoryUserStorage());
        userValidator = new UserValidator();
        validUser = TestUtil.createValidUser();
        user1 = TestUtil.createFirstUser();
        user2 = TestUtil.createSecondUser();
        user3 = TestUtil.createThirdUser();
    }

    @Test
    void testAddUser() {
        User addedUser = userController.add(validUser);
        assertEquals(1, addedUser.getId());
        userValidator.isValid(addedUser, null);
    }

    @Test
    void testUpdateUser() {
        User updatedUser = new User(
                1L,
                "updateduser@example.com",
                "updateduserlogin",
                "updatedusername",
                LocalDate.of(2001, 1, 1)
        );

        userController.add(validUser);

        User result = userController.update(updatedUser);
        assertEquals("updateduser@example.com", result.getEmail());
        assertEquals("updateduserlogin", result.getLogin());
    }

    @Test
    void testAddFriend() {
        userController.add(user1);
        userController.add(user2);
        userController.addFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2));
    }

    @Test
    void testRemoveFriend() {
        userController.add(user1);
        userController.add(user2);
        userController.addFriend(user1.getId(), user2.getId());
        userController.deleteFriend(user1.getId(), user2.getId());

        Collection<User> friends = userController.getFriends(user1.getId());
        assertEquals(0, friends.size());
    }

    @Test
    void testCommonFriends() {
        userController.add(user1);
        userController.add(user2);
        userController.add(user3);
        userController.addFriend(user1.getId(), user3.getId());
        userController.addFriend(user2.getId(), user3.getId());

        Collection<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user3));
    }

    @Test
    void testInvalidEmail() {
        User invalidEmailUser = new User(
                0L,
                "invalid-email",
                "user2login",
                "Name",
                LocalDate.of(2025, 1, 1)
        );

        assertThrows(ValidationException.class, () -> {
            userValidator.isValid(invalidEmailUser, null);
        });
    }

    @Test
    void testInvalidUserBirthday() {
        User invalidUserBirthday = new User(
                0L,
                "user@example.com",
                "user2login",
                "Name",
                LocalDate.of(2025, 1, 1)
        );

        assertThrows(ValidationException.class, () -> {
            userValidator.isValid(invalidUserBirthday, null);
        });
    }

    @Test
    void testAddAndRemoveFriend() {
        userController.add(user1);
        userController.add(user2);

        userController.addFriend(user1.getId(), user2.getId());
        Collection<User> friendsAfterAdding = userController.getFriends(user1.getId());
        assertEquals(1, friendsAfterAdding.size());
        assertTrue(friendsAfterAdding.contains(user2));

        userController.deleteFriend(user1.getId(), user2.getId());
        Collection<User> friendsAfterRemoving = userController.getFriends(user1.getId());
        assertEquals(0, friendsAfterRemoving.size());
    }

    @Test
    void testAutomaticUserName() {
        User userWithoutName = new User(
                0L,
                "user@example.com",
                "userlogin",
                null,
                LocalDate.of(2000, 1, 1)
        );

        userValidator.isValid(userWithoutName, null);

        assertEquals("userlogin", userWithoutName.getName());
    }

    @Test
    void testDeleteFriend() {
        userController.add(user1);
        userController.add(user2);

        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);

        List<User> friendsOfUser1 = userController.getFriends(1);
        List<User> friendsOfUser2 = userController.getFriends(2);

        assertTrue(friendsOfUser1.isEmpty());
        assertTrue(friendsOfUser2.isEmpty());
    }

    @Test
    void testGetCommonFriends() {
        User user4 = new User(4L, "user4@example.com", "user4", "User Four",
                LocalDate.of(1993, 1, 1));

        userController.add(user1);
        userController.add(user2);
        userController.add(user3);
        userController.add(user4);

        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);
        userController.addFriend(2, 4);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        assertEquals(1, commonFriends.size());
        assertEquals(3, commonFriends.getFirst().getId());
    }
}