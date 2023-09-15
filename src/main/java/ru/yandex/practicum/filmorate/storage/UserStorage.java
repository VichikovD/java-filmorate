package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User createUser(User user);

    public User updateUser(User thatUser);

    public List<User> getAllUsers();

    public Optional<User> getOptionalUserById(Integer id);

    public User getUserById(Integer id);
}
