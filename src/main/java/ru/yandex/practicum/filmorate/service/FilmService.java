package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    ValidateService validateService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public Film createFilm(Film film) {
        film.setLikesQuantity(0);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateService.validateFilmId(film);
        int filmId = film.getId();
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        filmStorage.addLike(film, user);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found by id: " + filmId));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        filmStorage.deleteLike(film, user);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
