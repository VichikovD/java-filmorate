package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreRowMapper;
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

        return namedParameterJdbcTemplate.query(sql, new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getById(int genreId) {
        String sqlSelect = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "WHERE genre_id = :genre_id";

        SqlParameterSource parameters = new MapSqlParameterSource("genre_id", genreId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, (ResultSetExtractor<Optional<Genre>>) rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            Genre genre = new GenreRowMapper().mapRow(rs, 1);  // 1 в mapRow бесполезна, в самом методе она даже не используется, но есть в сигнатуре
            return Optional.of(genre);
        });
    }
}
