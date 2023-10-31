package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    public Review create(Review review);

    public void update(Review review);

    public List<Review> getAll(int count);

    public Optional<Review> getById(int reviewId);

    public List<Review> getAllByFilmId(int filmId, int count);

    public void addLike(int reviewId, int userId);

    public void deleteLikeOrDislike(int reviewId, int userId);

    public void addDislike(int reviewId, int userId);

    public void deleteById(int reviewId);
}
