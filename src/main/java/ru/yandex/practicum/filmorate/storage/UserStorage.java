package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User createUser(User user);

    public void updateUser(User thatUser);

    public List<User> getAllUsers();

    public Optional<User> getUserById(int id);

    public void addFriend(User userId1, User userId2);

    public void deleteFriend(User userId1, User userId2);

    public List<Integer> getFriendsIdListById(int id);

    public List<User> getFriendsUsersListById(int id);
}
