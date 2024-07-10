package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    private UserController userController;
    private Validator validator;
    private UserValidator userValidator;
    private User validUser;


    @BeforeEach
    void setUp() {
        userController = new UserController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userValidator = new UserValidator();
        validUser = TestUtil.createValidUser();
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
    void testGetAllUsers() {
        userController.add(validUser);
        User user2 = new User(
                2,
                "user2@example.com",
                "user2login",
                "user2name",
                LocalDate.of(2001, 1, 1)
        );

        userController.add(user2);

        List<User> allUsers = userController.getAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    void testInvalidEmail() {
        User invalidEmailUser = new User(
                0,
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
                0,
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
    void testAutomaticUserName() {
        User userWithoutName = new User(
                0,
                "user@example.com",
                "user2login",
                null, // Name is null
                LocalDate.of(2000, 1, 1)
        );

        userValidator.isValid(userWithoutName, null);

        assertEquals("user2login", userWithoutName.getName());
    }
}