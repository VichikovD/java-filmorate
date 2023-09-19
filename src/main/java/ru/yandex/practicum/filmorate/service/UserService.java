package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;
    ValidateService validateService;

    @Autowired
    public UserService(UserStorage userStorage, ValidateService validateService) {
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public User createUser(User user) {
        validateService.validateUserName(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateService.userExistsByIdValidation(user.getId());
        validateService.validateUserName(user);
        userStorage.updateUser(user);
        return user;
    }

    public User getUserById(Integer id) {
        validateService.userExistsByIdValidation(id);
        return userStorage.getUserById(id)
                .get();
    }

    public void addFriend(Integer friendId1, Integer friendId2) {
        validateService.userExistsByIdValidation(friendId1);
        validateService.userExistsByIdValidation(friendId2);

        userStorage.addFriend(friendId1, friendId2);
        userStorage.addFriend(friendId2, friendId1);
    }

    public void deleteFriend(Integer friendId1, Integer friendId2) {
        validateService.userExistsByIdValidation(friendId1);
        validateService.userExistsByIdValidation(friendId2);

        userStorage.deleteFriend(friendId1, friendId2);
        userStorage.deleteFriend(friendId2, friendId1);
    }

    public List<User> getFriendsByUserId(Integer id) {
        validateService.userExistsByIdValidation(id);
        return userStorage.getFriendsUsersListById(id);
    }

    public List<User> getUserCommonFriends(Integer id, Integer otherId) {
        validateService.userExistsByIdValidation(id);
        validateService.userExistsByIdValidation(otherId);

        List<Integer> mainFriendIdList = userStorage.getFriendsIdListById(id);
        List<Integer> otherFriendIdList = userStorage.getFriendsIdListById(otherId);
        List<User> commonUsersList = new ArrayList<>();

        for (Integer otherFriendId : otherFriendIdList) {
            if (mainFriendIdList.contains(otherFriendId)) {
                User commonFriend = userStorage.getUserById(otherFriendId)
                        .get();
                commonUsersList.add(commonFriend);
            }
        }
        return commonUsersList;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
