package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@Slf4j
public class ValidateService {
    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public ValidateService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void filmExistsByIdValidation(int filmId) {
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
    }

    public void userExistsByIdValidation(int userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
    }


    public void validateUserName(User user) {
        if (user.isEmptyName()) {
            user.setLoginAsName();
            log.debug("User name is blank, therefore, it's replaced with Login");
        }
    }
}
