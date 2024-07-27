package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validation.ValidUser;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final String friend = "/{id}/friends/{friendId}";
    private final String friendIdPath = "friend-id";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User add(@ValidUser @RequestBody User user) {
        return userService.add(user);
    }

    @PutMapping
    public User update(@ValidUser @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(friend)
    public User addFriend(@PathVariable int id, @PathVariable(friendIdPath) int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(friend)
    public void deleteFriend(@PathVariable int id, @PathVariable(friendIdPath) int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int idToCheck) {
        return userService.getCommonFriends(id, idToCheck);
    }
}