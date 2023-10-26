package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@Service
@Slf4j
public class ValidateService {
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

    public void validateReviewId(Review review) {
        if (review.getReviewId() == null) {
            throw new InvalidIdException("reviewId should not be empty");
        }
    }
}
