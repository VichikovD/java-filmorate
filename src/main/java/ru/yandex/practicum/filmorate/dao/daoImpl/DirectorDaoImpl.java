package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

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

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, (ResultSetExtractor<Optional<Director>>) rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            Director director = new DirectorRowMapper().mapRow(rs, rs.getRow());  // rs.getRow() в mapRow бесполезна, в самом методе она даже не используется, но есть в сигнатуре
            return Optional.of(director);
        });
    }

    @Override
    public void deleteById(Integer id) {
        String sqlDelete = "DELETE FROM directors " +
                "WHERE director_id = :director_id";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id", id);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }

    @Override
    public List<Director> getAll() {
        String sqlSelect = "SELECT director_id, director_name " +
                "FROM directors " +
                "ORDER BY director_id";

        return namedParameterJdbcTemplate.query(sqlSelect, new DirectorRowMapper());
    }

    @Override
    public List<Director> getByIdList(List<Integer> directorIdList) {
        String sqlSelect = "SELECT director_id, director_name " +
                "FROM directors " +
                "WHERE director_id IN (:director_id_list)";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id_list", directorIdList);
        return namedParameterJdbcTemplate.query(sqlSelect, parameters, new DirectorRowMapper());
    }
}
