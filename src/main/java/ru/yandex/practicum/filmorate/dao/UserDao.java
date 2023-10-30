package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    public User create(User user);

    public void update(User thatUser);

    public List<User> getAll();

    public Optional<User> getById(int userId);

    void deleteById(Integer id);

    public void addFriend(User user, User friend);

    public void deleteFriend(User user, User friend);

    public List<User> getFriendsListById(int userId);

    public List<User> getUserCommonFriends(User user, User otherUser);

    List<Film> getRecommendations(int userId);
}
