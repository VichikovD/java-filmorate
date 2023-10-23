package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class UserService {
    UserDao userDao;
    ValidateService validateService;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserDao userDao,
                       ValidateService validateService) {
        this.userDao = userDao;
        this.validateService = validateService;
    }

    public User createUser(User user) {
        if (user.isEmptyName()) {
            user.setLoginAsName();
        }
        return userDao.create(user);
    }

    public User updateUser(User user) {
        validateService.validateUserId(user);
        int userId = user.getId();
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        if (user.isEmptyName()) {
            user.setLoginAsName();
        }
        userDao.update(user);
        return user;
    }

    public User getUserById(int userId) {
        return userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
    }

    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userDao.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userDao.deleteFriend(user, friend);
    }

    public List<User> getFriendsByUserId(int userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return userDao.getFriendsListById(userId);
    }

    public List<User> getUserCommonFriends(int userId, int otherUserId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User otherUser = userDao.getById(otherUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + otherUserId));

        return userDao.getUserCommonFriends(user, otherUser);
    }
}
