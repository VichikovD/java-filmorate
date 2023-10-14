package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    FilmDao filmDao;
    UserDao userDao;
    GenreDao genreDao;
    MpaDao mpaDao;
    ValidateService validateService;

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmDao filmDao,
                       @Qualifier("userDaoImpl") UserDao userDao,
                       @Qualifier("genreDaoImpl") GenreDao genreDao,
                       @Qualifier("mpaDaoImpl") MpaDao mpaDao,
                       ValidateService validateService) {
        this.filmDao = filmDao;
        this.userDao = userDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.validateService = validateService;
    }

    public Film createFilm(Film film) {
        if (!mpaDao.getAll().contains(film.getMpa())) {
            throw new NotFoundException("Mpa id not found. Please check available mpa id via GET /mpa ");
        }

        Set<Genre> filmGenres = film.getGenres();
        if (filmGenres == null) {
            film.setGenres(new HashSet<Genre>());
            return filmDao.create(film);
        } else if (genreDao.getAll().containsAll(filmGenres)) {
            Film filmToReturn = filmDao.create(film);
            genreDao.updateToFilm(film);
            return filmToReturn;
        } else {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        }
    }

    public Film updateFilm(Film film) {
        validateService.validateFilmId(film);
        int filmId = film.getId();
        Set<Genre> filmGenres = film.getGenres();

        filmDao.getById(filmId).orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));

        if (!mpaDao.getAll().contains(film.getMpa())) {
            throw new NotFoundException("Mpa id not found. Please check available mpa id via GET /mpa ");
        }

        if (filmGenres == null) {
            film.setGenres(new HashSet<Genre>());
            filmDao.update(film);
            return film;
        } else if (genreDao.getAll().containsAll(filmGenres)) {
            filmDao.update(film);
            genreDao.updateToFilm(film);
            return film;
        } else {
            throw new NotFoundException("Genres id not found. Please check available genre id via GET /genre ");
        }
    }

    public Film getFilmById(int filmId) {
        Film film = filmDao.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        film.setGenres(genreDao.getByFilmId(filmId));
        return film;
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
        List<Film> filmList = filmDao.getAll();
        genreDao.setGenresToAllFilms(filmList);
        return filmList;
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> filmList = filmDao.getMostPopular(count);
        genreDao.setGenresToAllFilms(filmList);
        return filmList;
    }
}
