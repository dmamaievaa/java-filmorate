package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validation.ValidUser;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final String friendPath = "/{id}/friends/{friend-id}";
    private final String friendIdPath = "friend-id";

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User add(@ValidUser @RequestBody User user) {
        return userService.add(user);
    }

    @PutMapping
    public User update(@ValidUser @RequestBody User newUser) {
        return userService.update(newUser);
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
    public List<User> getCommonFriends(@PathVariable long id,
                                       @PathVariable("other-id") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
