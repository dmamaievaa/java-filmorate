package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestUtil {
    public static User createValidUser() {
        return new User(0,
                "user@example.com",
                "userlogin",
                "username",
                LocalDate.of(2000, 1, 1));
    }
}
