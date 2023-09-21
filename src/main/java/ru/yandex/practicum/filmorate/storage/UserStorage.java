package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User createUser(User user);

    public void updateUser(User thatUser);

    public List<User> getAllUsers();

    public Optional<User> getUserById(int userId);

    public void addFriend(User user, User friend);

    public void deleteFriend(User user, User friend);

    public List<User> getFriendsUsersListById(int userId);
}
