package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private Validator validator;
    private UserValidator userValidator;
    private User validUser;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(new InMemoryUserStorage());
        userController = new UserController(userService);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUpdateUser() {
        User updatedUser = new User(
                1,
                "updateduser@example.com",
                "updatedlogin",
                "updatedusername",
                LocalDate.of(2001, 1, 1)
        );

        userController.add(validUser);

        User result = userController.update(updatedUser);
        assertEquals("updateduser@example.com", result.getEmail());
        assertEquals("updatedlogin", result.getLogin());
    }

    @Test
    void testGetAllUsers() {
        userController.add(validUser);
        userController.add(user2);

        List<User> allUsers = userController.getAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    void testInvalidEmail() {
        User invalidEmailUser = new User(
                0,
                "invalid-email",
                "userlogin",
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
                0,
                "user@example.com",
                "userlogin",
                "Name",
                LocalDate.of(2025, 1, 1)
        );

        assertThrows(ValidationException.class, () -> {
            userValidator.isValid(invalidUserBirthday, null);
        });
    }

    @Test
    void testAutomaticUserName() {
        User userWithoutName = new User(
                0,
                "user@example.com",
                "userlogin",
                null,
                LocalDate.of(2000, 1, 1)
        );

        userValidator.isValid(userWithoutName, null);

        assertEquals("userlogin", userWithoutName.getName());
    }

    @Test
    void testAddFriend() {
        userController.add(user1);
        userController.add(user2);

        userController.addFriend(1, 2);

        List<User> friendsOfUser1 = userController.getFriends(1);
        List<User> friendsOfUser2 = userController.getFriends(2);

        assertTrue(friendsOfUser1.stream().anyMatch(u -> u.getId() == 2));
        assertTrue(friendsOfUser2.stream().anyMatch(u -> u.getId() == 1));
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
    void testGetFriends() {
       userController.add(user1);
        userController.add(user2);
        userController.add(user3);

        userController.addFriend(1, 2);
        userController.addFriend(1, 3);

        List<User> friendsOfUser1 = userController.getFriends(1);
        assertEquals(2, friendsOfUser1.size());
        assertTrue(friendsOfUser1.stream().anyMatch(u -> u.getId() == 2));
        assertTrue(friendsOfUser1.stream().anyMatch(u -> u.getId() == 3));
    }

    @Test
    void testGetCommonFriends() {
        User user4 = new User(4, "user4@example.com", "user4", "User Four", LocalDate.of(1993, 1, 1));

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
        assertEquals(3, commonFriends.get(0).getId());
    }
}