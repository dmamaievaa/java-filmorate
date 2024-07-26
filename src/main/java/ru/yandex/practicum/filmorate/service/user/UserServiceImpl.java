package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User getUserById(int userId) {
        return userStorage
                .getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User id = " + userId + " not found"));
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Friend with id = {} successfully added", friendId);
        return user;
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Friend with id = {} successfully removed", friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        List<User> listFriends = user.getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
        log.info("Friend list obtained");
        return listFriends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int idToCheck) {
        List<User> commonFriends = new ArrayList<>(getFriends(userId));
        commonFriends.retainAll(getFriends(idToCheck));
        return commonFriends;
    }
}