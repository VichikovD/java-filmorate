package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {
    //  Не могу так быстро найти и, главное, понять как реализовать batch update в namedParameterJdbcTemplate.
    //  Может оставим JdbcTemplate тут?
    JdbcTemplate jdbcTemplate;
    ValidateService validateService;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate, ValidateService validateService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validateService = validateService;
    }

    @Override
    public Set<Genre> getAll() {
        // почему-то в итоге не возвращает сортированный через "ORDER BY genre_id DESC"
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres";
        Set<Genre> sorteSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorteSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(new ResultSetWrappingSqlRowSet(rs))));
        return sorteSet;
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
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
