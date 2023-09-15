package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Integer counterId = 0;
    private LinkedHashMap<Integer, User> users;

    public InMemoryUserStorage() {
        users = new LinkedHashMap<>();
    }

    private Integer getNewId() {
        return ++counterId;
    }

    @Override
    public User createUser(User user) {
        validateUserName(user);
        Integer newId = getNewId();
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @Override
    public User updateUser(User thatUser) {
        User thisUser = users.get(thatUser.getId());
        if (thisUser == null) {
            throw new NotFoundException("User not found by ID: " + thatUser.getId());
        }
        users.put(thatUser.getId(), thatUser);
        return thatUser;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getOptionalUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    public User getUserById(Integer id) {
        return users.get(id);
    }

    private void validateUserName(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User name is blank, therefore, it's replaced with Login");
        }
    }
}
