package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserManager userManager = new UserManager();

    @PostMapping
    public User createUser(@RequestBody @Valid User user, BindingResult bindingResult) throws ValidateUserException {
        User userToReturn = userManager.createUser(user, bindingResult);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user, BindingResult bindingResult) throws ValidateUserException {
        User userToReturn = userManager.updateUser(user, bindingResult);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userManager.getAllFilms();
    }
}
