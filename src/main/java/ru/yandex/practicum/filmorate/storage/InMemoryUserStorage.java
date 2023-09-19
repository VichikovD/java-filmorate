package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Integer counterId = 0;
    private LinkedHashMap<Integer, User> users;
    private HashMap<Integer, HashSet<Integer>> friends;
    private HashMap<Integer, HashSet<Integer>> likedFilms;

    public InMemoryUserStorage() {
        this.users = new LinkedHashMap<>();
        this.friends = new HashMap<>();
        this.likedFilms = new HashMap<>();
    }

    public void addFriend(Integer userId1, Integer userId2) {
        friends.get(userId1).add(userId2);
    }

    public void deleteFriend(Integer userId1, Integer userId2) {
        friends.get(userId1).remove(userId2);
    }

    public void addLikedFilm(Integer userId, Integer filmId) {
        likedFilms.get(userId).add(filmId);
    }

    public void deleteLikedFilm(Integer userId, Integer filmId) {
        likedFilms.get(userId).remove(filmId);
    }

    public List<Integer> getFriendsIdListById(Integer id) {
        return new ArrayList<>(friends.get(id));
    }

    public List<User> getFriendsUsersListById(Integer id) {
        List<Integer> friendIdList = getFriendsIdListById(id);
        List<User> friendUsersList = new ArrayList<>();
        for (Integer friendId : friendIdList) {
            friendUsersList.add(users.get(friendId));
        }
        return friendUsersList;
    }

    private Integer getNewId() {
        return ++counterId;
    }

    @Override
    public User createUser(User user) {
        Integer newId = getNewId();
        user.setId(newId);
        users.put(newId, user);
        friends.put(newId, new HashSet<>());
        likedFilms.put(newId, new HashSet<>());
        return user;
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }
}
