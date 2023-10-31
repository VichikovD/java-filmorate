package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Review create(Review review);

    void update(Review review);

    List<Review> getAll(int count);

    Optional<Review> getById(int reviewId);

    List<Review> getAllByFilmId(int filmId, int count);

    void addLike(int reviewId, int userId);

    void deleteLikeOrDislike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteById(int reviewId);
}
