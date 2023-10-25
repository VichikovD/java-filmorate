package ru.yandex.practicum.filmorate.dao.daoImpl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class MpaDaoImpl implements MpaDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public MpaDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Set<Mpa> getAll() {
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "ORDER BY mpa_id";

        return new LinkedHashSet<>(namedParameterJdbcTemplate.query(sqlSelect, new MpaRowMapper()));
    }

    @Override
    public Optional<Mpa> getById(int mpaId) {
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "WHERE mpa_id = :mpa_id";

        SqlParameterSource parameters = new MapSqlParameterSource("mpa_id", mpaId);

        SqlRowSet rsMpa = namedParameterJdbcTemplate.queryForRowSet(sqlSelect, parameters);

        if (rsMpa.next()) {
            Mpa mpa = makeMpa(rsMpa);
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    private Mpa makeMpa(SqlRowSet rs) {
        return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }
}


