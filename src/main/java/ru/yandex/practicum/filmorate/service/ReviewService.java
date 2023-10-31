package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {
    ReviewDao reviewDao;
    FilmDao filmDao;
    UserDao userDao;
    GenreDao genreDao;
    MpaDao mpaDao;
    EventDao eventDao;
    ValidateService validateService;

    @Autowired
    public ReviewService(@Qualifier("reviewDaoImpl") ReviewDao reviewDao,
                         @Qualifier("filmDaoImpl") FilmDao filmDao,
                         @Qualifier("userDaoImpl") UserDao userDao,
                         @Qualifier("genreDaoImpl") GenreDao genreDao,
                         @Qualifier("mpaDaoImpl") MpaDao mpaDao,
                         @Qualifier("eventDaoImpl") EventDao eventDao,
                         ValidateService validateService) {
        this.reviewDao = reviewDao;
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.eventDao = eventDao;
        this.validateService = validateService;
    }

    public Review createReview(Review review) {
        int filmId = review.getFilmId();
        int userId = review.getUserId();

        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        Review reviewToReturn = reviewDao.create(review);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.REVIEW)
                .operation(EventOperation.ADD)
                .entityId(review.getReviewId())
                .build();
        eventDao.create(event);

        return reviewToReturn;
    }

    public Review updateReview(Review review) {
        validateService.validateReviewId(review);
        int reviewId = review.getReviewId();

        Review reviewToReturn = reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found by id: " + reviewId));

        reviewDao.update(review);
        reviewToReturn.setContent(review.getContent());
        reviewToReturn.setIsPositive(review.getIsPositive());

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(reviewToReturn.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.UPDATE)
                .entityId(reviewId)
                .build();
        eventDao.create(event);

        return reviewToReturn;
    }

    public Review getById(int reviewId) {
        return reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found by id: " + reviewId));
    }

    public List<Review> getAll(int count) {
        return reviewDao.getAll(count);
    }

    public List<Review> getAllByFilmId(Integer filmId, Integer count) {
        return reviewDao.getAllByFilmId(filmId, count);
    }

    public void addLike(Integer reviewId, Integer userId) {
        Review review = reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found by id: " + reviewId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        reviewDao.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        Review review = reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found by id: " + reviewId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        reviewDao.addDislike(reviewId, userId);
    }

    public void deleteLikeOrDislike(Integer reviewId, Integer userId) {
        Review review = reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found by id: " + reviewId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        reviewDao.deleteLikeOrDislike(reviewId, userId);
    }

    public void deleteById(Integer reviewId) {
        Review review = reviewDao.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + reviewId));

        reviewDao.deleteById(reviewId);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(EventOperation.REMOVE)
                .entityId(reviewId)
                .build();
        eventDao.create(event);
    }
}
