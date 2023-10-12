package ru.yandex.practicum.filmorate.storage.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.dao.GenresDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class GenresDaoImpl implements GenresDao {
    JdbcTemplate jdbcTemplate;
    ValidateService validateService;

    public GenresDaoImpl(JdbcTemplate jdbcTemplate, ValidateService validateService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validateService = validateService;
    }

    @Override
    public void updateGenresToFilm(int filmId, Set<Genre> genres) {
        deleteGenresFromFilm(filmId);
        if (genres != null || Objects.equals(genres, new HashSet<Genre>())) {
            HashMap<String, Object> parameters = new HashMap<>();
            for (Genre genre : genres) {
                parameters.put("film_id", filmId);
                parameters.put("genre_id", genre.getId());
                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films_genres");
                try {
                    simpleJdbcInsert.execute(parameters);
                } catch (DuplicateKeyException e) {
                    log.debug(e.getMessage(), e);
                }
            }
        }
        /*String sqlDelete = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, filmId);
        String sqlCreate = "INSERT INTO films_genres(film_id, genre_id" +
                "VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlCreate, filmId, genres);
        }
        return getGenresByFilmId(filmId);*/
    }

    public Set<Genre> getAllGenres() {
        // почему-то в итоге не возвращает сортированный через "ORDER BY genre_id DESC"
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres";
        Set<Genre> sorteSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorteSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs)));
        return sorteSet;
    }

    public Set<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT * " +
                "FROM films_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        Set<Genre> sorteSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        sorteSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
        return sorteSet;
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    public Genre getGenreByGenreId(int genreId) {
        String sql = "SELECT genre_id, genre_name " +
                "FROM genres " +
                "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    public void deleteGenresFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM films_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, filmId);
    }
}
