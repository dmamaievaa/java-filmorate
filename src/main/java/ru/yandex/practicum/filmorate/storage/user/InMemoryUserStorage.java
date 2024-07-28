package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 1L;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        checkUserLogin(users, user);
        user.setId(userId);
        users.put(userId, user);
        userId++;
        log.info("User with id = {} successfully added", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            if (!users.get(user.getId()).getLogin().equals(user.getLogin())) {
                checkUserLogin(users, user);
            }
            users.put(user.getId(), user);
            log.info("User with id = {} successfully updated", user.getId());
        } else {
            log.warn("User with id = {} not updated, as they are not registered", user.getId());
            throw new NotFoundException("Cannot update user data. User does not exist");
        }
        return user;
    }

    private void checkUserLogin(Map<Long, User> users, User user) {
        for (User value : users.values()) {
            if (user.getLogin().equals(value.getLogin())) {
                throw new ValidationException("User with this login is already registered");
            }
        }
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }
}