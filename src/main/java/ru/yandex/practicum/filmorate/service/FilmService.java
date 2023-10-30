package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

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

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmDao filmDao,
                       @Qualifier("userDaoImpl") UserDao userDao,
                       @Qualifier("genreDaoImpl") GenreDao genreDao,
                       @Qualifier("mpaDaoImpl") MpaDao mpaDao,
                       @Qualifier("directorDaoImpl") DirectorDao directorDao,
                       ValidateService validateService) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
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
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

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


    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        directorDao.getById(directorId).orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));
        String sortString;
        switch (sortBy) {
            case "film_id":
                sortString = "f.film_id ASC";
                break;
            case "year":
                sortString = "f.release_date ASC";
                break;
            case "likes":
                sortString = "likes_quantity DESC";
                break;
            default:
                sortString = "f.film_id ASC";
        }
        return filmDao.getByDirectorId(directorId, sortString);
    }
}
