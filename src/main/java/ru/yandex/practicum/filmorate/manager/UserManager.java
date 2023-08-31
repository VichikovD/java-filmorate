package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class UserManager {
    HashMap<Integer, User> users;

    public UserManager() {
        users = new HashMap<>();
    }

    @PostMapping
    public User createUser(User user, BindingResult bindingResult) throws ValidateUserException {
        handleValidateErrors(bindingResult);
        validateUserName(user);
        int newId = users.size() + 1;
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid User thatUser, BindingResult bindingResult) throws ValidateUserException {
        handleValidateErrors(bindingResult);
        validateUserName(thatUser);
        User thisUser = users.get(thatUser.getId());
        if (thisUser == null) {
            throw new ValidateUserException("User not found by ID: " + thatUser.getId());
        }
        users.put(thatUser.getId(), thatUser);
        return thatUser;
    }

    @GetMapping
    public List<User> getAllFilms() {
        return new ArrayList<>(users.values());
    }

    private void validateUserName(User user) throws ValidateUserException {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User name is blank, therefore, it's replaced with Login");
        }
    }

    private void handleValidateErrors(BindingResult bindingResult) throws ValidateUserException {
        if (bindingResult.hasErrors()) {
            List<String> errorsList = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                String errorMessage = error.getDefaultMessage();
                log.debug(errorMessage);
                errorsList.add(errorMessage);
            }
            throw new ValidateUserException(String.join(", ", errorsList));
        }
    }
}
