package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDaoImpl implements FilmDao {
    NamedParameterJdbcOperations namedParameterJdbcTemplate;

    public FilmDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        String sqlInsert = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) " +
                "VALUES (:film_name, :description, :release_date, :duration, :mpa_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));

        updateGenres(film);
        updateDirectors(film);
        return film;
    }

    @Override
    public void update(Film film) {
        int filmId = film.getId();
        String sqlUpdate = "UPDATE films " +
                "SET film_name = :film_name, description = :description, release_date = :release_date, " +
                "duration = :duration, mpa_id = :mpa_id " +
                "WHERE film_id = :film_id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId())
                .addValue("film_id", filmId);

        namedParameterJdbcTemplate.update(sqlUpdate, parameters);

        updateDirectors(film);
        updateGenres(film);
    }

    @Override
    public Optional<Film> getById(Integer filmId) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, " +
                "m.mpa_name, COUNT(l.user_id) as likes_quantity " +
                "FROM films AS f " +
                "LEFT OUTER JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE f.film_id = :film_id " +
                "GROUP BY f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);
        List<Film> filmList = getFilmsWithDirectors(sqlSelect, parameters, namedParameterJdbcTemplate);

        if (!filmList.isEmpty()) {
            return Optional.of(filmList.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
                "COUNT(l.user_id) as likes_quantity " +
                "FROM films AS f " +
                "LEFT OUTER JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource();
        List<Film> filmList = getFilmsWithDirectors(sqlSelect, parameters, namedParameterJdbcTemplate);
        updateGenresToAllFilms(filmList);

        return filmList;
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, " +
                "m.mpa_name, COUNT(l.user_id) as likes_quantity " +
                "FROM films AS f " +
                "LEFT OUTER JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT :limit";
        SqlParameterSource parameters = new MapSqlParameterSource("limit", count);
        List<Film> filmList = getFilmsWithDirectors(sqlSelect, parameters, namedParameterJdbcTemplate);
        updateGenresToAllFilms(filmList);

        return filmList;
    }

    @Override
    public List<Film> getByDirectorId(Integer id, String sort) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "d.director_id, d.director_name, m.mpa_id, m.mpa_name, " +
                "COUNT(l.user_id) as likes_quantity " +
                "FROM films AS f " +
                "LEFT OUTER JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE d.director_id = :director_id " +
                "GROUP BY f.film_id" +
                "ORDER BY :sort";


        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("director_id", id)
                .addValue("sort", sort);

        List<Film> filmList = getFilmsWithDirectors(sqlSelect, parameters, namedParameterJdbcTemplate);

        updateGenresToAllFilms(filmList);

        return filmList;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlInsert = "MERGE INTO likes AS l " +
                "USING VALUES (:film_id, :user_id) AS source(film_id, user_id) " +
                "ON l.film_id = source.film_id AND l.user_id = source.user_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT " +
                "VALUES (source.film_id, source.user_id)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("user_id", user.getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlInsert = "DELETE FROM likes " +
                "WHERE film_id = :film_id AND user_id = :film_id";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("user_id", user.getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    // для getById(), чтобы загрузить жанры
    private Set<Genre> getGenreByFilmId(int filmId) {
        String sql = "SELECT * " +
                "FROM films_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = :film_id " +
                "ORDER BY fg.genre_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        return new LinkedHashSet<>(namedParameterJdbcTemplate.query(sql, parameters, new GenreRowMapper()));
    }

    // для create()/update(), чтобы обновить жанры одним запросом
    private void updateGenres(Film film) {
        int filmId = film.getId();
        Set<Genre> genreSet = film.getGenres();

        deleteGenresFromFilm(filmId);

        List<Genre> genreList = new ArrayList<>(genreSet);
        String sqlInsert = "INSERT INTO films_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        namedParameterJdbcTemplate.getJdbcOperations()
                .batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
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

    private void updateDirectors(Film film) {
        int filmId = film.getId();
        Set<Director> directorSet = film.getDirectors();

        deleteDirectorsFromFilm(filmId);

        List<Director> directorList = new ArrayList<>(directorSet);
        String sqlInsert = "INSERT INTO films_directors (film_id, director_id " +
                "VALUES (?, ?)";

        namedParameterJdbcTemplate.getJdbcOperations()
                .batchUpdate(sqlInsert, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, directorList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return directorList.size();
                    }
                });


    }

    // для getAll() и getMostPopular(), чтобы добавить жанры сразу всем фильмам одним запросом
    private void updateGenresToAllFilms(Collection<Film> filmCollection) {
        Map<Integer, Film> filmMap = filmCollection.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Collection<Integer> idList = filmMap.keySet();

        String sqlSelect = "SELECT fg.film_id, fg.genre_id, g.genre_name " +
                "FROM films_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", idList);
        namedParameterJdbcTemplate.query(sqlSelect, parameters, (rs, rowNum) -> {
            Film film = filmMap.get(rs.getInt("film_id"));
            Set<Genre> genres = film.getGenres();
            genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
            return null;
        });
    }

    // Чтобы предварительно отчистить films_genres перед updateGenre
    private void deleteGenresFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM films_genres " +
                "WHERE film_id = :filmId";

        SqlParameterSource parameters = new MapSqlParameterSource("filmId", filmId);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }

    private void deleteDirectorsFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM films_directors " +
                "WHERE film_id = :film_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        namedParameterJdbcTemplate.update(sqlDelete,parameters);
    }

    private Film makeFilm(SqlRowSet rs) {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .likesQuantity(rs.getInt("likes_quantity"))
                .build();
    }

    private List<Film> getFilmsWithDirectors(String sql, SqlParameterSource parameters, NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        Map<Integer, Film> filmMap = new HashMap<>();

        namedParameterJdbcTemplate.query(sql, parameters, (rs) -> {
            while (rs.next()) {
                int filmId = rs.getInt("id");

                if (!filmMap.containsKey(filmId)) {
                    Film film = makeFilm(new ResultSetWrappingSqlRowSet(rs));
                    filmMap.put(filmId, film);
                }

                if (rs.getObject("director_id") != null) {
                    Director director = new Director(rs.getInt("director_id"), rs.getString("director_name"));
                    filmMap.get(filmId).getDirectors().add(director);
                }
            }
            return null;
        });
        return new ArrayList<>(filmMap.values());
    }
}