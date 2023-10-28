package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    FilmDao filmDao;
    UserDao userDao;
    GenreDao genreDao;
    MpaDao mpaDao;
    ValidateService validateService;
    EventDao eventDao;

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmDao filmDao,
                       @Qualifier("userDaoImpl") UserDao userDao,
                       @Qualifier("genreDaoImpl") GenreDao genreDao,
                       @Qualifier("mpaDaoImpl") MpaDao mpaDao,
                       @Qualifier("eventDaoImpl") EventDao eventDao,
                       ValidateService validateService) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.eventDao = eventDao;
        this.validateService = validateService;
    }

    public Film createFilm(Film film) {
        int mpaId = film.getMpa().getId();
        mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));

        Set<Genre> filmGenres = film.getGenres();
        if (filmGenres != null
                && !genreDao.getAll().containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else {
            return filmDao.create(film);
        }
    }

    public Film updateFilm(Film film) {
        validateService.validateFilmId(film);
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();
        Set<Genre> filmGenres = film.getGenres();

        filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));

        if (filmGenres != null
                && !genreDao.getAll().containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
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

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.ADD)
                .entityId(filmId)
                .build();
        eventDao.create(event);

        filmDao.addLike(film, user);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(EventOperation.REMOVE)
                .entityId(filmId)
                .build();
        eventDao.create(event);

        filmDao.deleteLike(film, user);
    }

    public List<Film> getAllFilms() {
        return filmDao.getAll();
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmDao.getMostPopular(count);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        return filmDao.getCommon(userId, friendId);
    }

}
