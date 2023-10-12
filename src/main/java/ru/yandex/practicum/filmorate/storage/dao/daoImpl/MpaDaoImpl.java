package ru.yandex.practicum.filmorate.storage.dao.daoImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class MpaDaoImpl implements MpaDao {
    JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Mpa> getAllMpa(){
        // почему-то в итоге не возвращает сортированный через "ORDER BY genre_id DESC"
        String sqlSelect = "SELECT mpa_id, mpa_name " +
                "FROM mpas";
        return new HashSet<Mpa> (jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeMpa(rs)));
    }

    @Override
    public Mpa getMpaByMpaId(int mpaId) {
        String sql = "SELECT mpa_id, mpa_name " +
                "FROM mpas " +
                "WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), mpaId);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
