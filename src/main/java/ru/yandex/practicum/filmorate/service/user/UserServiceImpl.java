package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Friend with id = {} successfully added", friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user =  userStorage.getUserById(userId);
        User friend =  userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Friend with id = {} successfully removed", friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user =  userStorage.getUserById(userId);
        log.info("Friend list obtained");
        return user.getFriends().stream().map(userStorage::getUserById).toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long idToCheck) {
        List<User> commonFriends = new ArrayList<>(getFriends(userId));
        commonFriends.retainAll(getFriends(idToCheck));
        return commonFriends;
    }
}