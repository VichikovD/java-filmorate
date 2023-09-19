package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateService.filmExistsByIdValidation(film.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Integer filmId, Integer userId) {
        validateService.filmExistsByIdValidation(filmId);
        validateService.userExistsByIdValidation(userId);

        filmStorage.addLike(filmId, userId);
        userStorage.addLikedFilm(userId, filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        validateService.filmExistsByIdValidation(filmId);
        validateService.userExistsByIdValidation(userId);

        filmStorage.deleteLike(filmId, userId);
        userStorage.deleteLikedFilm(userId, filmId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Film getFilmById(Integer id) {
        validateService.filmExistsByIdValidation(id);
        return filmStorage.getFilmById(id)
                .get();
    }
}
