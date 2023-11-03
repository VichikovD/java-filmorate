package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ReviewDaoImpl implements ReviewDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public ReviewDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Review create(Review review) {
        String sqlInsert = "INSERT INTO reviews (content, is_positive, film_id, user_id) " +
                "VALUES (:content, :is_positive, :film_id, :user_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters, keyHolder);
        review.setReviewId(keyHolder.getKeyAs(Integer.class));
        return review;
    }

    @Override
    public void update(Review review) {
        String sqlUpdate = "UPDATE reviews " +
                "SET content = :content, is_positive = :is_positive " +
                "WHERE review_id = :review_id";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("review_id", review.getReviewId());
        namedParameterJdbcTemplate.update(sqlUpdate, parameters);
    }

    @Override
    public Optional<Review> getById(int reviewId) {
        String sqlSelect = "SELECT r.review_id, r.content, r.is_positive, r.film_id, r.user_id, " +
                "SUM(CASE WHEN is_like THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT is_like THEN 1 ELSE 0 END) AS useful " +
                "FROM reviews AS r " +
                "LEFT OUTER JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "WHERE r.review_id = :review_id " +
                "GROUP BY r.review_id";

        SqlParameterSource parameters = new MapSqlParameterSource("review_id", reviewId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, (ResultSetExtractor<Optional<Review>>) rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            Review review = new ReviewRowMapper().mapRow(rs, 1);  // 1 в mapRow бесполезна, в самом методе она даже не используется, но есть в сигнатуре
            return Optional.of(review);
        });
    }

    //  Логика подсчета лайков(useful) -> количество(like) - количество(dislike)
    @Override
    public List<Review> getAll(int count) {
        String sqlSelect = "SELECT r.review_id, r.content, r.is_positive, r.film_id, r.user_id, " +
                "SUM(CASE WHEN is_like THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT is_like THEN 1 ELSE 0 END) AS useful " +
                "FROM reviews AS r " +
                "LEFT OUTER JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT :count";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("count", count);
        return namedParameterJdbcTemplate.query(sqlSelect, sqlParameterSource, new ReviewRowMapper());
    }

    //  Логика подсчета лайков(useful) -> количество(like) - количество(dislike)
    @Override
    public List<Review> getAllByFilmId(int filmId, int count) {
        String sqlSelect = "SELECT r.review_id, r.content, r.is_positive, r.film_id, r.user_id, " +
                "SUM(CASE WHEN is_like THEN 1 ELSE 0 END) - SUM(CASE WHEN NOT is_like THEN 1 ELSE 0 END) AS useful " +
                "FROM reviews AS r " +
                "LEFT OUTER JOIN review_likes AS rl ON r.review_id = rl.review_id " +
                "WHERE film_id = :film_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT :count";


        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("count", count)
                .addValue("film_id", filmId);
        return namedParameterJdbcTemplate.query(sqlSelect, sqlParameterSource, new ReviewRowMapper());
    }

    //  Если есть review_id и user_id уже есть в таблице review_likes, происходит обновление значения is_like на true (значит лайк) без разбора был ли он лайком или дизлайком
    //  если нет совпадений, добавляется новая запись с лайком
    @Override
    public void addLike(int reviewId, int userId) {
        String sqlInsert = "MERGE INTO review_likes AS rl " +
                "USING VALUES (:review_id, :user_id) AS source(review_id, user_id) " +
                "ON rl.review_id = source.review_id AND rl.user_id = source.user_id " +
                "WHEN MATCHED THEN UPDATE " +
                "SET rl.is_like = true " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (review_id, user_id, is_like) " +
                "VALUES (source.review_id, source.user_id, true)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    //  Как с лайком, только вместо лайка дизлайк
    @Override
    public void addDislike(int reviewId, int userId) {
        String sqlInsert = "MERGE INTO review_likes AS rl " +
                "USING VALUES (:review_id, :user_id) AS source(review_id, user_id) " +
                "ON rl.review_id = source.review_id AND rl.user_id = source.user_id " +
                "WHEN MATCHED THEN UPDATE " +
                "SET rl.is_like = false " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (review_id, user_id, is_like) " +
                "VALUES (source.review_id, source.user_id, false)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public void deleteLikeOrDislike(int reviewId, int userId) {
        String sqlInsert = "DELETE FROM review_likes " +
                "WHERE review_id = :review_id AND user_id = :user_id";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public void deleteById(int reviewId) {
        String sqlInsert = "DELETE FROM reviews " +
                "WHERE review_id = :review_id";

        SqlParameterSource parameters = new MapSqlParameterSource("review_id", reviewId);
        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }
}

