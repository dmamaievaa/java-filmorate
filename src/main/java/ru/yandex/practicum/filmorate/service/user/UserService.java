package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Service
public interface UserService {
    Collection<User> getAll();

    User add(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    void deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
