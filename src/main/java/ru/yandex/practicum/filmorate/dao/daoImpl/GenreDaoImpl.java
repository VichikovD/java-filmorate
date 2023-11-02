package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    ValidateService validateService;

    public GenreDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ValidateService validateService) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.validateService = validateService;
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "ORDER BY genre_id";

        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(new ResultSetWrappingSqlRowSet(rs)));
    }

    @Override
    public Optional<Genre> getById(int genreId) {
        String sqlSelect = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "WHERE genre_id = :genre_id";

        SqlParameterSource parameters = new MapSqlParameterSource("genre_id", genreId);
        SqlRowSet rsGenre = namedParameterJdbcTemplate.queryForRowSet(sqlSelect, parameters);
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
