package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    FilmDao filmDao;
    UserDao userDao;
    GenreDao genreDao;
    MpaDao mpaDao;
    DirectorDao directorDao;
    ValidateService validateService;
    EventDao eventDao;

    @Autowired
    public FilmService(FilmDao filmDao,
                       UserDao userDao,
                       GenreDao genreDao,
                       MpaDao mpaDao,
                       EventDao eventDao,
                       DirectorDao directorDao,
                       ValidateService validateService) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.eventDao = eventDao;
        this.directorDao = directorDao;
        this.validateService = validateService;
    }

    public Film createFilm(Film film) {
        int mpaId = film.getMpa().getId();
        mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));

        Set<Genre> filmGenres = film.getGenres();
        Set<Director> filmDirectors = film.getDirectors();

        if (filmGenres != null
                && !genreDao.getAll().containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (filmDirectors != null
                && !directorDao.getAll().containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        } else {
            return filmDao.create(film);
        }
    }

    public Film updateFilm(Film film) {
        validateService.validateFilmId(film);
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();
        Set<Genre> filmGenres = film.getGenres();
        Set<Director> filmDirectors = film.getDirectors();

        filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));

        if (filmGenres != null
                && !genreDao.getAll().containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (filmDirectors != null
                && !directorDao.getAll().containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        } else {
            filmDao.update(film);
            return film;
        }
    }

    public Film getFilmById(int filmId) {
        return filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
    }

    public void deleteById(Integer id) {
        filmDao.deleteById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        filmDao.addLike(film, user);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.ADD)
                .entityId(filmId)
                .build();
        eventDao.create(event);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        filmDao.deleteLike(film, user);

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.REMOVE)
                .entityId(filmId)
                .build();
        eventDao.create(event);
    }

    public List<Film> getAllFilms() {
        return filmDao.getAll();
    }

    public List<Film> getAllViaSubstringSearch(String query, String filter) {
        HashMap<String, String> filterMap = new HashMap<>();
        String correctedFilter = filter.toLowerCase()
                .replaceAll(" ", "");
        String correctedQuery = query.toLowerCase()
                .replaceAll(";", "");

        switch (correctedFilter) {
            case "director":
                filterMap.put("director", "%" + correctedQuery + "%");
                filterMap.put("title", "NULL");
                break;
            case "title":
                filterMap.put("director", "NULL");
                filterMap.put("title", "%" + correctedQuery + "%");
                break;
            case "director,title":
            case "title,director":
                filterMap.put("director", "%" + correctedQuery + "%");
                filterMap.put("title", "%" + correctedQuery + "%");
                break;
            default:
                throw new ValidateException("Invalid filer: %s. Filter may have the following values: director, title");
        }
        return filmDao.getFilmsViaSubstringSearch(filterMap);
    }

    public List<Film> getMostPopularFilms(int count, Integer genreId, Integer year) {
        return filmDao.getMostPopular(count, genreId, year);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        return filmDao.getCommon(userId, friendId);
    }


    public List<Film> getDirectorFilms(int directorId, SortMode sortBy) {
        directorDao.getById(directorId).orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));
        return filmDao.getByDirectorId(directorId, sortBy);
    }
}
