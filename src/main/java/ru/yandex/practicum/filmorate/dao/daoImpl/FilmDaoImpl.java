package ru.yandex.practicum.filmorate.dao.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dao.mapper.FilmRowMapper;
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

    private static final String SELECT_FILMS = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, " +
            "m.mpa_name, COUNT(l.user_id) as likes_quantity " +
            "FROM films AS f " +
            "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
            "LEFT OUTER JOIN film_likes AS l ON f.film_id = l.film_id ";

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
        String sqlSelect = SELECT_FILMS +
                "WHERE f.film_id = :film_id " +
                "GROUP BY f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        return namedParameterJdbcTemplate.query(sqlSelect, parameters, (ResultSetExtractor<Optional<Film>>) rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            Film film = new FilmRowMapper().mapRow(rs, rs.getRow());  // rs.getRow() в mapRow бесполезна, в самом методе она даже не используется, но есть в сигнатуре
            film.setGenres(getGenreByFilmId(filmId));
            film.setDirectors(getDirectorsByFilmId(filmId));
            return Optional.of(film);
        });
    }

    @Override
    public void deleteById(Integer id) {
        String sqlDelete = "DELETE FROM films " +
                "WHERE film_id = :film_id ";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", id);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }

    @Override
    public List<Film> getAll() {
        String sqlSelect = SELECT_FILMS +
                "GROUP BY f.film_id";
        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);
        return filmList;
    }

    @Override
    public List<Film> getAllMostPopular(Integer count, Integer genreId, Integer year) {
        String sqlSelect = SELECT_FILMS +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "WHERE (:genreId IS NULL OR fg.genre_id = :genreId) " +
                "AND (:year IS NULL OR YEAR(f.release_date) = :year) " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_quantity DESC " +
                "LIMIT :limit";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("limit", count)
                .addValue("genreId", genreId)
                .addValue("year", year);

        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, parameters, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);
        return filmList;
    }

    @Override
    public List<Film> getViaSubstringSearch(String query, List<SubstringSearch> filters) {
        String correctedQuery = query.toLowerCase()
                .replaceAll(";", "");
        HashMap<String, String> filterMap = new HashMap<>();
        filterMap.put("title", "NULL");
        filterMap.put("director", "NULL");

        for (SubstringSearch filter : filters) {
            switch (filter) {
                case director:
                    filterMap.put("director", "%" + correctedQuery + "%");
                    break;
                case title:
                    filterMap.put("title", "%" + correctedQuery + "%");
                    break;
            }
        }

        String sqlSelect = SELECT_FILMS +
                "LEFT OUTER JOIN film_directors  AS fd ON f.film_id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE LOWER(f.film_name) LIKE :title OR LOWER(d.director_name) LIKE :director " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("director", filterMap.get("director"))
                .addValue("title", filterMap.get("title"));
        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, parameters, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);

        return filmList;
    }

    @Override
    public List<Film> getByDirectorId(Integer id, SortMode sortBy) {
        String sortString = null;
        switch (sortBy) {
            case year:
                sortString = "f.release_date ASC";
                break;
            case likes:
                sortString = "likes_quantity DESC";
                break;
        }

        String sqlSelect = SELECT_FILMS +
                "LEFT OUTER JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = :director_id " +
                "GROUP BY f.film_id " +
                "ORDER BY " + sortString;

        SqlParameterSource parameters = new MapSqlParameterSource("director_id", id);

        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, parameters, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);
        return filmList;
    }

    @Override
    public List<Film> getCommon(Integer userId, Integer friendId) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
                "COUNT(l3.user_id) as likes_quantity " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "INNER JOIN film_likes l1 ON f.film_id = l1.film_id " +
                "INNER JOIN film_likes l2 ON f.film_id = l2.film_id " +
                "LEFT JOIN film_likes l3 ON f.film_id = l3.film_id " +
                "WHERE l1.user_id = :userId AND l2.user_id = :friendId " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_quantity DESC ";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);

        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, params, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);

        return filmList;
    }

    @Override
    public List<Film> getRecommendationsById(int userId) {
        String sqlSelect = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, " +
                "COUNT(l.user_id) as likes_quantity " +
                "FROM film_likes AS l " +
                "LEFT OUTER JOIN films AS f ON l.film_id = f.film_id " +
                "LEFT OUTER JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "WHERE l.user_id = " +
                /**/"(SELECT l2.user_id " +
                /**/"FROM film_likes AS l1 " +
                /**/"INNER JOIN film_likes AS l2 ON l1.film_id = l2.film_id " +
                /**/"WHERE l1.user_id = :user_id AND l2.user_id != :user_id " +
                /**/"GROUP BY l2.user_id " +
                /**/"ORDER BY COUNT(l2.user_id) DESC " +
                /**/"LIMIT 1) " +
                "AND l.film_id NOT IN " +
                /**/"(SELECT film_id " +
                /**/"FROM film_likes " +
                /**/"WHERE user_id = :user_id) " +
                /**/"GROUP BY f.film_id";

        SqlParameterSource parameters = new MapSqlParameterSource("user_id", userId);

        List<Film> filmList = namedParameterJdbcTemplate.query(sqlSelect, parameters, new FilmRowMapper());

        loadGenresToAllFilms(filmList);
        loadDirectorsToAllFilms(filmList);
        return filmList;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlInsert = "MERGE INTO film_likes AS l " +
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
        String sqlInsert = "DELETE FROM film_likes " +
                "WHERE film_id = :film_id AND user_id = :user_id";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("user_id", user.getId());

        namedParameterJdbcTemplate.update(sqlInsert, parameters);
    }

    // для getAll() и getAllMostPopular(), чтобы добавить жанры сразу всем фильмам одним запросом
    private void loadGenresToAllFilms(Collection<Film> filmCollection) {
        Map<Integer, Film> filmMap = filmCollection.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Collection<Integer> idList = filmMap.keySet();

        String sqlSelect = "SELECT fg.film_id, fg.genre_id, g.genre_name " +
                "FROM film_genres AS fg " +
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

    private void loadDirectorsToAllFilms(Collection<Film> filmCollection) {
        Map<Integer, Film> filmMap = filmCollection.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        Collection<Integer> idList = filmMap.keySet();

        String sqlSelect = "SELECT fd.film_id, fd.director_id, d.director_name " +
                "FROM film_directors AS fd " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id IN (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", idList);

        namedParameterJdbcTemplate.query(sqlSelect, parameters, (rs, rowNum) -> {
            Film film = filmMap.get(rs.getInt("film_id"));
            Set<Director> directors = film.getDirectors();
            directors.add(new Director(rs.getInt("director_id"), rs.getString("director_name")));
            return null;
        });
    }

    // для getById(), чтобы загрузить жанры
    private Set<Genre> getGenreByFilmId(int filmId) {
        String sql = "SELECT * " +
                "FROM film_genres AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = :film_id " +
                "ORDER BY fg.genre_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        return new LinkedHashSet<>(namedParameterJdbcTemplate.query(sql, parameters, new GenreRowMapper()));
    }


    private Set<Director> getDirectorsByFilmId(int filmId) {
        String sql = "SELECT * " +
                "FROM film_directors AS fd " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id = :film_id " +
                "ORDER BY fd.director_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        return new LinkedHashSet<>(namedParameterJdbcTemplate.query(sql, parameters, new DirectorRowMapper()));
    }


    // для create()/update(), чтобы обновить жанры одним запросом
    private void updateGenres(Film film) {
        int filmId = film.getId();
        Set<Genre> genreSet = film.getGenres();

        deleteGenresFromFilm(filmId);

        List<Genre> genreList = new ArrayList<>(genreSet);
        String sqlInsert = "INSERT INTO film_genres (film_id, genre_id) " +
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
        String sqlInsert = "INSERT INTO film_directors (film_id, director_id) " +
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

    // Чтобы предварительно отчистить films_genres перед updateGenre
    private void deleteGenresFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM film_genres " +
                "WHERE film_id = :film_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }

    private void deleteDirectorsFromFilm(int filmId) {
        String sqlDelete = "DELETE FROM film_directors " +
                "WHERE film_id = :film_id";

        SqlParameterSource parameters = new MapSqlParameterSource("film_id", filmId);

        namedParameterJdbcTemplate.update(sqlDelete, parameters);
    }
}

