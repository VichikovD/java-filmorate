package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    void update(User thatUser);

    List<User> getAll();

    Optional<User> getById(int userId);

    void deleteById(Integer id);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getFriendsById(int userId);

    List<User> getCommonFriends(User user, User otherUser);
}
