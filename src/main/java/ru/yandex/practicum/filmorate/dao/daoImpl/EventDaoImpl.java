package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Component
public class EventDaoImpl implements EventDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public EventDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void create(Event event) {
        String sqlInsert = "INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (:timestamp, :user_id, :event_type, :event_operation, :entity_id)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("timestamp", event.getTimestamp())
                .addValue("user_id", event.getUserId())
                .addValue("event_type", event.getEventType().toString())
                .addValue("event_operation", event.getOperation().toString())
                .addValue("entity_id", event.getEntityId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public List<Event> getByUserId(Integer userId) {
        String sqlSelect = "SELECT event_id, user_id, event_type, operation, timestamp, entity_id " +
                "FROM events " +
                "WHERE user_id = :userId";

        SqlParameterSource parameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, new EventRowMapper());
    }
}
