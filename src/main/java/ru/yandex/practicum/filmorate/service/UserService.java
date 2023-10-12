package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;
    ValidateService validateService;

    @Autowired
    public UserService(@Qualifier("userDbStorage")UserStorage userStorage,
                       ValidateService validateService) {
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public User createUser(User user) {
        if (user.isEmptyName()) {
            user.setLoginAsName();
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateService.validateUserId(user);
        int userId = user.getId();
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        if (user.isEmptyName()) {
            user.setLoginAsName();
        }
        return userStorage.updateUser(user);

    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userStorage.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userStorage.deleteFriend(user, friend);
    }

    public List<User> getFriendsByUserId(int userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return userStorage.getFriendsUsersListById(userId);
    }

    public List<User> getUserCommonFriends(int userId, int otherUserId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userStorage.getUserById(otherUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + otherUserId));

        List<User> mainUserFriendsList = userStorage.getFriendsUsersListById(userId);
        List<User> otherUserFriendsList = userStorage.getFriendsUsersListById(otherUserId);
        List<User> commonUsersList = new ArrayList<>();

        for (User otherUserFriend : otherUserFriendsList) {
            if (mainUserFriendsList.contains(otherUserFriend)) {
                commonUsersList.add(otherUserFriend);
            }
        }
        return commonUsersList;
    }
}
