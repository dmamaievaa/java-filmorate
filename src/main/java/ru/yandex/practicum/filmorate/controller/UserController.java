package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer userId = 1;

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User add(@ValidUser @RequestBody User user) {
        user.setId(userId++);
        checkUserLogin(user);
        users.put(user.getId(), user);
        log.info("User with id " + user.getId() + " successfully added");
        return user;
    }

    @PutMapping
    public User update(@ValidUser @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            checkUserLogin(user);
            users.put(user.getId(), user);
            log.info("User with id " + user.getId() + " successfully updated");
        } else {
            log.warn("User with id " +  user.getId() + " not found for update");
            throw new ValidationException("Cannot update user, no user with such id");
        }
        return user;
    }

    private void checkUserLogin(User user) {
        for (User existingUser : users.values()) {
            if (user.getLogin().equals(existingUser.getLogin())) {
                throw new ValidationException("User with such login already registered");
            }
        }
    }
}