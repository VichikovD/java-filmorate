package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserStorage userStorage = new UserStorage();

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        User userToReturn = userStorage.createUser(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        User userToReturn = userStorage.updateUser(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllFilms();
    }
}
