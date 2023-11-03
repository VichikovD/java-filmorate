package ru.yandex.practicum.filmorate.dao.daoImpl;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component
public class MpaDaoImpl implements MpaDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public MpaDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "ORDER BY mpa_id";

        return namedParameterJdbcTemplate.query(sqlSelect, new MpaRowMapper());
    }

    @Override
    public Optional<Mpa> getById(int mpaId) {
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "WHERE mpa_id = :mpa_id";

        SqlParameterSource parameters = new MapSqlParameterSource("mpa_id", mpaId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, (ResultSetExtractor<Optional<Mpa>>) rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            Mpa mpa = new MpaRowMapper().mapRow(rs, rs.getRow());  // rs.getRow() в mapRow бесполезна, в самом методе она даже не используется, но есть в сигнатуре
            return Optional.of(mpa);
        });
    }
}


