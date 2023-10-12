package ru.yandex.practicum.filmorate.storage.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user_name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("birthday", user.getBirthday());

        int userId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKey(parameters)
                .intValue();

        return getUserById(userId).get();
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();

        String sqlUpdate = "UPDATE users " +
                "SET user_name = ?, email = ?, login = ?, birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdate, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), userId);

        return getUserById(userId).get();
    }

    @Override
    public List<User> getAllUsers() {
        String sqlSelect = "SELECT user_id, user_name, email, login, birthday " +
                "FROM users ";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(int userId) {
        String sqlSelect = "SELECT user_id, email, login, user_name, birthday " +
                "FROM users " +
                "WHERE user_id = ?";
        User user = jdbcTemplate.queryForObject(sqlSelect, (rs, numRow) -> makeUser(rs), userId);
        return Optional.of(user);
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlInsert = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlInsert, user.getId(), friend.getId());
        } catch (DuplicateKeyException e) {
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlInsert = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ? ";
        jdbcTemplate.update(sqlInsert, user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriendsUsersListById(int userId) {
        String sqlSelect = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday " +
                "FROM friends AS f " +
                "LEFT OUTER JOIN users AS u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeUser(rs), userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("user_name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .email(rs.getString("email"))
                .build();
    }
}
