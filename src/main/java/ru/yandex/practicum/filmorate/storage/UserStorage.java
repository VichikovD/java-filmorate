package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class UserStorage {
    Integer idCounter = 0;
    LinkedHashMap<Integer, User> users;

    public UserStorage() {
        users = new LinkedHashMap<>();
    }

    private int getNewId() {
        return ++idCounter;
    }

    @PostMapping
    public User createUser(User user) {
        validateUserName(user);
        int newId = getNewId();
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @PutMapping
    public User updateUser(User thatUser) {
        User thisUser = users.get(thatUser.getId());
        if (thisUser == null) {
            throw new NotFoundException("User not found by ID: " + thatUser.getId());
        }
        users.put(thatUser.getId(), thatUser);
        return thatUser;
    }

    @GetMapping
    public List<User> getAllFilms() {
        return new ArrayList<>(users.values());
    }

    private void validateUserName(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User name is blank, therefore, it's replaced with Login");
        }
    }

    private void handleValidateErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorsList = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                String errorMessage = error.getDefaultMessage();
                log.debug(errorMessage);
                errorsList.add(errorMessage);
            }
            throw new ValidateException(String.join(", ", errorsList));
        }
    }
}
