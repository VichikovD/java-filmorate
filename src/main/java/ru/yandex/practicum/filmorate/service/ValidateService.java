package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Service
@Slf4j
public class ValidateService {
    public static int GENRES_SIZE = 6;
    public static int MPA_SIZE = 5;

    @Autowired
    public ValidateService() {
    }

    public void validateFilmId(Film film) {
        if (film.getId() == null) {
            throw new InvalidIdException("Id should not be empty");
        }
    }

    public void validateUserId(User user) {
        if (user.getId() == null) {
            throw new InvalidIdException("Id should not be empty");
        }
    }

    public void validateGenres(Set<Genre> genres) {
        if (genres == null) {
            return;
        }
        for (Genre genre : genres) {
            if (genre.getId() < 1 || genre.getId() > GENRES_SIZE) {
                throw new ValidateException("Genre id not in range (1 - " + GENRES_SIZE + ")");
            }
        }
    }

    public void validateMpa(Mpa mpa) {
        if (mpa == null) {
            throw new ValidateException("Film mpa must be declared (id range: 1 - " + MPA_SIZE + ")");
        }
        if (mpa.getId() < 1 || mpa.getId() > MPA_SIZE) {
            throw new ValidateException("Mpa id not in range (1 - " + MPA_SIZE + ")");
        }

    }
}
