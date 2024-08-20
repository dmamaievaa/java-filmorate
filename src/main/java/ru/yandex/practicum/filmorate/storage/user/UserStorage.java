package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAll();
    User add(User user);
    User update(User user);
    User getUserById(Long userId);
    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    List<User> getFriendsByUserId(Long id);
    List<User> getCommonFriends(Long userId, Long friendId);
}