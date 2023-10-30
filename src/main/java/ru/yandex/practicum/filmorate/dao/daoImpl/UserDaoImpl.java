package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.dao.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    FilmDao filmDao;

    public UserDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       @Qualifier("filmDaoImpl") FilmDao filmDao) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.filmDao = filmDao;
    }

    @Override
    public User create(User user) {
        String sqlInsert = "INSERT INTO users (user_name, email, login, birthday) " +
                "VALUES (:user_name, :email, :login, :birthday)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sqlInsert, parameters, keyHolder);
        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public void update(User user) {
        String sqlUpdate = "UPDATE users " +
                "SET user_name = :user_name, email = :email, login = :login, birthday = :birthday " +
                "WHERE user_id = :user_id";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday())
                .addValue("user_id", user.getId());

        namedParameterJdbcTemplate.update(sqlUpdate, parameters);
    }

    @Override
    public List<User> getAll() {
        String sqlSelect = "SELECT user_id, user_name, email, login, birthday " +
                "FROM users ";
        return namedParameterJdbcTemplate.query(sqlSelect, new UserRowMapper());
    }

    @Override
    public Optional<User> getById(int userId) {
        String sqlSelect = "SELECT user_id, email, login, user_name, birthday " +
                "FROM users " +
                "WHERE user_id = :userId";
        SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);
        SqlRowSet rsUser = namedParameterJdbcTemplate.queryForRowSet(sqlSelect, parameters);

        if (rsUser.next()) {
            User user = makeUser(rsUser);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sqlDelete = "DELETE FROM users " +
                "WHERE user_id = :user_id ";

        SqlParameterSource parameters = new MapSqlParameterSource("user_id", id);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }


    @Override
    public void addFriend(User user, User friend) {
        String sqlInsert = "MERGE INTO friends AS f " +
                "USING VALUES (:user_id, :friend_id) AS source(user_id, friend_id) " +
                "ON f.user_id = source.user_id AND f.friend_id = source.friend_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT VALUES (source.user_id, source.friend_id)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("friend_id", friend.getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlInsert = "DELETE FROM friends " +
                "WHERE user_id = :user_id AND friend_id = :friend_id ";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("friend_id", friend.getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public List<User> getFriendsListById(int userId) {
        String sqlSelect = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday " +
                "FROM friends AS f " +
                "LEFT OUTER JOIN users AS u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = :userId";

        SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, new UserRowMapper());
    }

    @Override
    public List<User> getUserCommonFriends(User user, User otherUser) {
        String sqlSelect = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday " +
                "FROM friends AS f " +
                "INNER JOIN users AS u ON f.friend_id = u.user_id " +
                "INNER JOIN friends AS o_u_friends ON f.friend_id = o_u_friends.friend_id " +
                "WHERE f.user_id = :user_id AND o_u_friends.user_id = :other_user_id";


        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("other_user_id", otherUser.getId());

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, new UserRowMapper());
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        String sqlQuery = "SELECT film_id FROM likes WHERE user_id = "
                + "(SELECT user_id FROM likes WHERE user_id != :user_id AND film_id IN "
                + "(SELECT film_id FROM likes WHERE user_id = :user_id) "
                + "GROUP BY user_id ORDER BY COUNT(film_id) DESC LIMIT 1) "
                + "AND film_id NOT IN (SELECT film_id FROM likes WHERE user_id = 3);";
        SqlParameterSource parameters = new MapSqlParameterSource("user_id", userId);
        SqlRowSet rsFilms = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, parameters);
        List<Integer> recommendedFilmsIds = new ArrayList<>();
        while (rsFilms.next()) {
            recommendedFilmsIds.add(rsFilms.getInt("film_id"));
        }
        return filmDao.getByIds(recommendedFilmsIds);
    }


    @Override
    public List<Event> getAllEventsByUserId(Integer userId) {
        String sqlSelect = "SELECT event_id, user_id, event_type, operation, timestamp, entity_id " +
                "FROM events " +
                "WHERE user_id = :userId";

        SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, new EventRowMapper());
    }

    private User makeUser(SqlRowSet rs) {
        return User.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("user_name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .email(rs.getString("email"))
                .build();
    }
}
