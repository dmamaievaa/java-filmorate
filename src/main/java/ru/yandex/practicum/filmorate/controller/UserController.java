package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User add(@Valid @RequestBody User user) {
        validateUser(user);
        checkUserLogin(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("User with id " + user.getId() + " successfully added");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validateUser(user);
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

    private void validateUser(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Email not specified");
            throw new ValidationException("Email not specified");
        } else if (!email.contains("@")) {
            log.warn("Incorrect email");
            throw new ValidationException("Incorrect email");
        } else if (user.getLogin() == null ||
                user.getLogin().contains(" ") ||
                user.getLogin().isBlank()) {
            log.warn("Login cannot be empty or contain spaces");
            throw new ValidationException("Login cannot be empty or contain spaces");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Login will be used as the name for user with id " + user.getId());
        } else if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Incorrect birthday date");
            throw new ValidationException("Incorrect birthday date");
        }
    }
}