package ru.yandex.practicum.filmorate.dao.daoImpl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.Set;

@Component
public class MpaDaoImpl implements MpaDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public MpaDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Set<Mpa> getAll() {
        // почему-то в итоге не возвращает сортированный через "ORDER BY genre_id DESC"
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas";

        return new HashSet<Mpa>(namedParameterJdbcTemplate.query(sqlSelect, new MpaRowMapper()));
    }

    @Override
    public Mpa getById(int mpaId) {
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "WHERE mpa_id = :mpa_id";

        SqlParameterSource parameters = new MapSqlParameterSource("mpa_id", mpaId);

        return namedParameterJdbcTemplate.queryForObject(sqlSelect, parameters, new MpaRowMapper());
    }
    /*private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }*/
}


