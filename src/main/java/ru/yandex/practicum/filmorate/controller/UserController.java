package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        User userToReturn = userService.createUser(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        User userToReturn = userService.updateUser(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Optional<String> id) {
        Integer idInt = getIdByOptionalString(id);
        return userService.getUserById(idInt);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Optional<String> id,
                          @PathVariable Optional<String> friendId) {
        Integer idInt = getIdByOptionalString(id);
        Integer friendIdInt = getIdByOptionalString(friendId);
        userService.addFriend(idInt, friendIdInt);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Optional<String> id,
                             @PathVariable Optional<String> friendId) {
        Integer idInt = getIdByOptionalString(id);
        Integer friendIdInt = getIdByOptionalString(friendId);
        userService.deleteFriend(idInt, friendIdInt);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Optional<String> id) {
        Integer idInt = getIdByOptionalString(id);
        return userService.getFriendsByUserId(idInt);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUserCommonFriends(@PathVariable Optional<String> id,
                                           @PathVariable Optional<String> otherId) {
        Integer idInt = getIdByOptionalString(id);
        Integer otherIdInt = getIdByOptionalString(otherId);

        return userService.getUserCommonFriends(idInt, otherIdInt);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    private Integer getIdByOptionalString(Optional<String> optString) {
        String idString = optString.orElseThrow(() -> new InvalidIdException("Not identified Id: " + optString));
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new InvalidIdException(String.format("Invalid Id: %s.", idString));
        }
    }
}
