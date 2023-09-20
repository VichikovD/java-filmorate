package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int counterId = 0;
    private LinkedHashMap<Integer, User> users;
    private HashMap<Integer, HashSet<Integer>> friends;

    public InMemoryUserStorage() {
        this.users = new LinkedHashMap<>();
        this.friends = new HashMap<>();
    }

    @Override
    public User createUser(User user) {
        int newId = getNewId();
        user.setId(newId);
        users.put(newId, user);
        friends.put(newId, new HashSet<>());
        return user;
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void addFriend(User user, User friend) {
        friends.get(user.getId())
                .add(friend.getId());
    }

    public void deleteFriend(User user, User friend) {
        friends.get(user.getId())
                .remove(friend.getId());
    }

    public List<Integer> getFriendsIdListById(int id) {
        return new ArrayList<>(friends.get(id));
    }

    public List<User> getFriendsUsersListById(int id) {
        List<Integer> friendIdList = getFriendsIdListById(id);
        List<User> friendUsersList = new ArrayList<>();
        for (Integer friendId : friendIdList) {
            friendUsersList.add(users.get(friendId));
        }
        return friendUsersList;
    }

    private int getNewId() {
        return ++counterId;
    }
}
