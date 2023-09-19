package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User createUser(User user);

    public void updateUser(User thatUser);

    public List<User> getAllUsers();

    public Optional<User> getUserById(Integer id);

    public void addFriend(Integer userId1, Integer userId2);

    public void deleteFriend(Integer userId1, Integer userId2);

    public List<Integer> getFriendsIdListById(Integer id);

    public List<User> getFriendsUsersListById(Integer id);

    public void addLikedFilm(Integer userId, Integer filmId);

    public void deleteLikedFilm(Integer userId, Integer filmId);
}
