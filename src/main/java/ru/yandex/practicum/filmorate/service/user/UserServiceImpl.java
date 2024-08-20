package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User add(User user) {
        checkUserLogin(user);
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        User existingUser = userStorage.getUserById(user.getId());
        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }
        if (!existingUser.getLogin().equals(user.getLogin())) {
            checkUserLogin(user);
        }
        return userStorage.update(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return userStorage.getFriendsByUserId(id);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

    private void checkUserLogin(User user) {
        boolean loginExists = userStorage.getAll().stream()
                .anyMatch(existingUser -> user.getLogin().equals(existingUser.getLogin()));

        if (loginExists) {
            throw new ValidationException("User with this login is already registered");
        }
    }
}
