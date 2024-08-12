package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserDbStorage {
    Collection<User> getAll();

    User add(User user);

    User update(User user);

    User getUserById(Long userId);
}