package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testAddUser() {
        User validUser = User.builder()
                .email("user@example.com")
                .login("userlogin")
                .name("username")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User invalidUser = User.builder()
                .email(null)
                .login("userlogin")
                .name("username")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.add(invalidUser));
        User addedUser = userController.add(validUser);
        assertEquals(1, addedUser.getId());
    }

    @Test
    void testUpdateUser() {
        User existingUser = User.builder()
                .id(1)
                .email("user@example.com")
                .login("userlogin")
                .name("username")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User updatedUser = User.builder()
                .id(1)
                .email("updateduser@example.com")
                .login("updateduserlogin")
                .name("updatedusername")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userController.add(existingUser);

        User result = userController.update(updatedUser);
        assertEquals("updateduser@example.com", result.getEmail());
        assertEquals("updateduserlogin", result.getLogin());
    }

    @Test
    void testGetAllUsers() {
        User user1 = User.builder()
                .id(1)
                .email("user1@example.com")
                .login("user1login")
                .name("user1name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User user2 = User.builder()
                .id(2)
                .email("user2@example.com")
                .login("user2login")
                .name("user2name")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userController.add(user1);
        userController.add(user2);

        List<User> allUsers = userController.getAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        User user = User.builder()
                .email(null)
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = User.builder()
                .email("invalidemail")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = User.builder()
                .email("user@example.com")
                .login("invalid login")
                .name("name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = User.builder()
                .email("user@example.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(3000, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.add(user));
    }
}