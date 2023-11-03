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
import java.util.stream.Collectors;

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

        // Обертка в HashSet тут и в методе update() используется только для снижения time complexity.
        // List.containsAll выполняется за O(N * M), а перекладывание в HashSet и его проверка - за О(N) + O(N)
        // т.е. 1 genre/director => List.containsAll = 1; HashSet.containsAll = 2  - Медленнее в 2 раза
        // 2 genre/director => List.containsAll = 4; HashSet.containsAll = 4 - Одинаковая скорость
        // 3 genre/director => List.containsAll = 9; HashSet.containsAll = 6 - в 1.5 быстрее
        // 4 genre/director => List.containsAll = 16; HashSet.containsAll = 8 - в 2 быстрее и т.д.
        if (!new HashSet<>(genreDao.getByIdList(filmGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList())))
                .containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (!new HashSet<>(directorDao.getByIdList(filmDirectors.stream()
                .map(Director::getId)
                .collect(Collectors.toList())))
                .containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        }

        return filmDao.create(film);
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

        if (!new HashSet<>(genreDao.getByIdList(filmGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList())))
                .containsAll(filmGenres)) {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        } else if (!new HashSet<>(directorDao.getByIdList(filmDirectors.stream()
                .map(Director::getId)
                .collect(Collectors.toList())))
                .containsAll(filmDirectors)) {
            throw new NotFoundException("Directors id not found. Please check available director id via GET /director ");
        }

        filmDao.update(film);
        return film;
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

    public List<Film> getViaSubstringSearch(String query, List<SubstringSearch> filter) {
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
}
