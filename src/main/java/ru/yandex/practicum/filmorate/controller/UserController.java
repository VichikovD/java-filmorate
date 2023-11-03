package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("POST {}, body={}", "\"/users\"", user);
        User userToReturn = userService.create(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("PUT {}, body={}", "\"/users\"", user);
        User userToReturn = userService.update(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        log.info("GET {}", "\"/users/" + id + "\"");
        User userToReturn = userService.getById(id);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.info("DELETE \"/users/" + id + "\"");
        userService.deleteById(id);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET {}", "\"/users\"");
        List<User> usersList = userService.getAll();
        log.debug(usersList.toString());
        return usersList;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        log.info("PUT {}", "\"/users/" + id + "/friends/" + friendId + "\"");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        log.info("DELETE {}", "\"/users/" + id + "/friends/" + friendId + "\"");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsById(@PathVariable int id) {
        log.info("GET {}", "\"/users/" + id + "/friends/\"");
        List<User> usersList = userService.getFriendsById(id);
        log.debug(usersList.toString());
        return usersList;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id,
                                       @PathVariable int otherId) {
        log.info("GET {}", "\"/users/" + id + "/friends/common/" + otherId + "\"");
        List<User> usersList = userService.getCommonFriends(id, otherId);
        log.debug(usersList.toString());
        return usersList;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) {
        log.info("GET {}", "\"/users/" + id + "/feed/\"");
        List<Event> eventsList = userService.getEventsById(id);
        log.debug(eventsList.toString());
        return eventsList;
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendationsById(@PathVariable int userId) {
        log.info("GET {}", "\"/users/" + userId + "/recommendations/\"");
        List<Film> recommendedFilms = userService.getRecommendationsById(userId);
        log.debug(recommendedFilms.toString());
        return recommendedFilms;
    }
}
