package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    UserDao userDao;
    ValidateService validateService;
    EventDao eventDao;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserDao userDao,
                       @Qualifier("eventDaoImpl") EventDao eventDao,
                       ValidateService validateService) {
        this.userDao = userDao;
        this.validateService = validateService;
        this.eventDao = eventDao;
    }

    public User create(User user) {
        if (user.isEmptyName()) {
            user.setLoginAsName();
        }
        return userDao.create(user);
    }

    public User update(User user) {
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

    public User getById(int userId) {
        return userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
    }

    public void deleteById(Integer id) {
        userDao.deleteById(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userDao.addFriend(user, friend);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(EventOperation.ADD)
                .entityId(friendId)
                .build();
        eventDao.create(event);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User friend = userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + friendId));

        userDao.deleteFriend(user, friend);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(EventOperation.REMOVE)
                .entityId(friendId)
                .build();
        eventDao.create(event);
    }

    public List<User> getFriendsById(int userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return userDao.getFriendsById(userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        User otherUser = userDao.getById(otherUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + otherUserId));

        return userDao.getCommonFriends(user, otherUser);
    }

    public List<Event> getEventsById(Integer userId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return userDao.getEventsById(userId);
    }

    public List<Film> getRecommendationsById(int userId) {
        return userDao.getRecommendationsById(userId);
    }
}
