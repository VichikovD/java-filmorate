package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.HashSet;
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

    public Film create(Film film) {
        int mpaId = film.getMpa().getId();
        mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));

        Set<Genre> filmGenres = film.getGenres();
        Set<Director> filmDirectors = film.getDirectors();

        if (filmGenres != null
                && !new HashSet<>(genreDao.getAll()).containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (filmDirectors != null
                && !new HashSet<>(directorDao.getAll()).containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        } else {
            return filmDao.create(film);
        }
    }

    public Film update(Film film) {
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
                && !new HashSet<>(genreDao.getAll()).containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (filmDirectors != null
                && !new HashSet<>(directorDao.getAll()).containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        } else {
            filmDao.update(film);
            return film;
        }
    }

    public Film getById(int filmId) {
        return filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
    }

    public void deleteById(Integer id) {
        filmDao.deleteById(id);
    }

    public List<Film> getAll() {
        return filmDao.getAll();
    }

    public List<Film> getAllMostPopular(int count, Integer genreId, Integer year) {
        return filmDao.getAllMostPopular(count, genreId, year);
    }

    public List<Film> getViaSubstringSearch(String query, SubstringSearch filter) {
        return filmDao.getViaSubstringSearch(query, filter);
    }


    public List<Film> getByDirectorId(int directorId, SortMode sortBy) {
        directorDao.getById(directorId).orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));
        return filmDao.getByDirectorId(directorId, sortBy);
    }

    public List<Film> getCommon(Integer userId, Integer friendId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        return filmDao.getCommon(userId, friendId);
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

    public List<Film> getCommon(Integer userId, Integer friendId) {
        userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userDao.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        return filmDao.getCommon(userId, friendId);
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
}
