package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Friend with id = {} successfully added", friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Friend not found");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Friend with id = {} successfully removed", friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        log.info("Friend list obtained for user with id = {}", userId);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long idToCheck) {
        List<User> commonFriends = new ArrayList<>(getFriends(userId));
        commonFriends.retainAll(getFriends(idToCheck));
        return commonFriends;
    }

    private void checkUserLogin(User user) {
        boolean loginExists = userStorage.getAll().stream()
                .anyMatch(existingUser -> user.getLogin().equals(existingUser.getLogin()));

        if (loginExists) {
            throw new ValidationException("User with this login is already registered");
        }
    }
}
