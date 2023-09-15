package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Integer id) {
        return userStorage.getOptionalUserById(id).orElseThrow(
                () -> new NotFoundException("User not found by id: " + id));
    }

    public void addFriend(Integer friendId1, Integer friendId2) {
        User user1 = userStorage.getOptionalUserById(friendId1).orElseThrow(
                () -> new NotFoundException("User not found by id: " + friendId1));
        User user2 = userStorage.getOptionalUserById(friendId2).orElseThrow(
                () -> new NotFoundException("User not found by id: " + friendId2));

        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void deleteFriend(Integer friendId1, Integer friendId2) {
        User user1 = userStorage.getOptionalUserById(friendId1).orElseThrow(
                () -> new NotFoundException("User not found by id: " + friendId1));
        User user2 = userStorage.getOptionalUserById(friendId2).orElseThrow(
                () -> new NotFoundException("User not found by id: " + friendId2));

        user1.deleteFriend(user2.getId());
        user2.deleteFriend(user1.getId());
    }

    public List<User> getFriendsByUserId(Integer id) {
        List<Integer> userFriendsIdList = getUserFriendsIdListById(id);
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : userFriendsIdList) {
            friendsList.add(userStorage.getUserById(friendId));
        }
        return friendsList;
    }

    public List<User> getUserCommonFriends(Integer idLong, Integer otherIdLong) {
        List<Integer> mainFriendIdList = getUserFriendsIdListById(idLong);
        List<Integer> otherFriendIdList = getUserFriendsIdListById(otherIdLong);
        List<User> commonUsersList = new ArrayList<>();

        for (Integer otherFriendId : otherFriendIdList) {
            if (mainFriendIdList.contains(otherFriendId)) {
                User commonFriend = userStorage.getUserById(otherFriendId);
                commonUsersList.add(commonFriend);
            }
        }
        return commonUsersList;
    }

    public List<Integer> getUserFriendsIdListById(Integer id) {
        User mainUser = userStorage.getOptionalUserById(id).orElseThrow(
                () -> new NotFoundException("User not found by id: " + id));
        return new ArrayList<>(mainUser.getFriends());
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
