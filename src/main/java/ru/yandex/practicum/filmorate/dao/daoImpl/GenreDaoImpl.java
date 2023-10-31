package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {
    JdbcTemplate jdbcTemplate;
    ValidateService validateService;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate, ValidateService validateService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validateService = validateService;
    }

    @Override
    public Set<Genre> getAll() {
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "ORDER BY genre_id";

        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(new ResultSetWrappingSqlRowSet(rs))));
    }

    @Override
    public Optional<Genre> getById(int genreId) {
        String sqlSelect = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "WHERE genre_id = ?";

        SqlRowSet rsGenre = jdbcTemplate.queryForRowSet(sqlSelect, genreId);
        if (rsGenre.next()) {
            Genre genre = makeGenre(rsGenre);
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    public Genre makeGenre(SqlRowSet rs) {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
