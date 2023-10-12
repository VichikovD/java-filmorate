package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.GenresDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    GenresDao genresDao;
    JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenresDao genresDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresDao = genresDao;
    }

    @Override
    public Film createFilm(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());
        Set<Genre> genres = film.getGenres();

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        genresDao.updateGenresToFilm(filmId, genres);

        return getFilmById(filmId).get();
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();

        String sqlUpdate = "UPDATE films " +
                "SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdate, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), filmId);
        genresDao.updateGenresToFilm(filmId, film.getGenres());

        return getFilmById(filmId).get();
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id";
        List<Film> filmList = jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFilm(rs));
        for (Film film : filmList) {
            try {
                film.setGenres(genresDao.getGenresByFilmId(film.getId()));
            } catch (EmptyResultDataAccessException e) {
                log.info("Film {} doesn't have genres", film);
            }
        }
        return filmList;
    }

    //  Optional чтобы сохранить интерфейс предыдущей реализации
    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(sqlSelect, (rs, numRow) -> makeFilm(rs), filmId);
        try {
            film.setGenres(genresDao.getGenresByFilmId(filmId));
        } catch (EmptyResultDataAccessException e) {
            log.info("Film {} doesn't have genres", film);
        }
        return Optional.of(film);
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlInsert = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlInsert, film.getId(), user.getId());
        } catch (DuplicateKeyException e) {
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlInsert = "DELETE FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlInsert, film.getId(), user.getId());
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        List<Film> filmList = jdbcTemplate.query(sqlSelect, (rs, numRow) -> makeFilm(rs), count);
        for (Film film : filmList) {
            try {
                film.setGenres(genresDao.getGenresByFilmId(film.getId()));
            } catch (EmptyResultDataAccessException e) {
                film.setGenres(new HashSet<>());
                log.info("Film {} doesn't have genres", film);
            }
        }
        return filmList;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();
    }
}
