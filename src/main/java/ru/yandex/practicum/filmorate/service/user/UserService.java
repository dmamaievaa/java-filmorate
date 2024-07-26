package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public interface UserService {

    User getUserById(int userId);

    List<User> getAll();

    User add(User user);

    User update(User user);

    User addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int idToCheck);
}