package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class DirectorDaoImpl implements DirectorDao {

    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public DirectorDaoImpl(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Director create(Director director) {
        String sqlInsert = "INSERT INTO directors (director_name) " +
                "VALUES (:director_name)";
        SqlParameterSource parameters = new MapSqlParameterSource("director_name", director.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sqlInsert, parameters, keyHolder);
        director.setId(keyHolder.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public void update(Director director) {
        String sqlUpdate = "UPDATE directors " +
                "SET director_name = :director_name " +
                "WHERE director_id = :director_id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("director_name", director.getName())
                .addValue("director_id", director.getId());

        namedParameterJdbcTemplate.update(sqlUpdate, parameters);
    }

    @Override
    public Optional<Director> getById(Integer id) {
        String sqlSelect = "SELECT director_id, director_name " +
                "FROM directors " +
                "WHERE director_id = :director_id";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id", id);
        SqlRowSet rsDirector = namedParameterJdbcTemplate.queryForRowSet(sqlSelect, parameters);

        if (rsDirector.next()) {
            Director director = makeDirector(rsDirector);
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sqlDelete = "DELETE * FROM directors " +
                "WHERE director_id = :director_id";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id", id);
        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }

    @Override
    public Set<Director> getAll() {
        String sqlSelect = "SELECT director_id, director_name " +
                "FROM directors " +
                "ORDER BY director_id";

        return new LinkedHashSet<>(namedParameterJdbcTemplate.query(sqlSelect, new DirectorRowMapper()));
    }

    private Director makeDirector(SqlRowSet rs) {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
    }
}
