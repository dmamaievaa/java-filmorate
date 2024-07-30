package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestUtil {
    public static User createValidUser() {
        return new User(0L,
                "user@example.com",
                "userlogin",
                "username",
                LocalDate.of(2000, 1, 1));
    }

    public static User createFirstUser() {
        return new User(1L,
                "user1@example.com",
                "user1",
                "User One",
                LocalDate.of(1990, 1, 1));
    }

    public static User createSecondUser() {
        return new User(2L,
                "user2@example.com",
                "user2",
                "User Two",
                LocalDate.of(1990, 1, 1));
    }

    public static User createThirdUser() {
        return new User(3L,
                "user3@example.com",
                "user3",
                "User Three",
                LocalDate.of(1990, 1, 1));
    }
}
