package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void updateToFilm(Film film) {
        int filmId = film.getId();
        List<Genre> genreList = new ArrayList<>(film.getGenres());
        deleteFromFilm(filmId);
        String sqlInsert = "INSERT INTO films_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genreList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    @Override
    public void setGenresToAllFilms(Collection<Film> filmCollection) {
        filmCollection.forEach((film -> film.setGenres(new HashSet<Genre>())));
        Map<Integer, Film> filmMap = filmCollection.stream().
                collect(Collectors.toMap(Film::getId, Function.identity()));
        Collection<Integer> idList = filmMap.keySet();

        String inSql = String.join(",", Collections.nCopies(idList.size(), "?"));
        String sqlSelect = String.format("SELECT fg.film_id, fg.genre_id, g.genre_name " +
                "FROM films_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (%s)", inSql);

        jdbcTemplate.query(sqlSelect, idList.toArray(), (rs, rowNum) -> {
            Film film = filmMap.get(rs.getInt("film_id"));
            Set<Genre> genres = film.getGenres();
            genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
            return null;
        });
    }

    @Override
    public Set<Genre> getAll() {
        // почему-то в итоге не возвращает сортированный через "ORDER BY genre_id DESC"
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres";
        Set<Genre> sorteSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorteSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs)));
        return sorteSet;
    }

    @Override
    public Set<Genre> getByFilmId(int filmId) {
        String sql = "SELECT * " +
                "FROM films_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        Set<Genre> sorteSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorteSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
        return sorteSet;
    }

    @Override
    public Genre getById(int genreId) {
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    @Override
    public void deleteFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM films_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, filmId);
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
