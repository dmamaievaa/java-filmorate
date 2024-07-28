package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidUser;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private UserStorage userStorage;
    private final String friendPath = "/{id}/friends/{friend-id}";
    private final String friendIdPath = "friend-id";

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @PostMapping
    public User add(@ValidUser @RequestBody User user) {
        return userStorage.add(user);
    }


    @PutMapping
    public User update(@ValidUser @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @PutMapping(friendPath)
    public void addFriend(@PathVariable long id, @PathVariable(friendIdPath) long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(friendPath)
    public void deleteFriend(@PathVariable long id, @PathVariable(friendIdPath) long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{other-id}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable("other-id") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
