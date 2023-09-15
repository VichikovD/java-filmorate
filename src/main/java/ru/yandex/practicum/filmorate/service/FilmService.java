package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Integer filmIdInt, Integer userIdInt) {
        Film film = filmStorage.getOptionalFilmById(filmIdInt).orElseThrow(
                () -> new NotFoundException("Film not found by id: " + filmIdInt));
        User user = userStorage.getOptionalUserById(userIdInt).orElseThrow(
                () -> new NotFoundException("User not found by id: " + userIdInt));

        film.addLike(user.getId());
        user.addLikedFilm(film.getId());
    }

    public void deleteLike(Integer filmIdInt, Integer userIdInt) {
        Film film = filmStorage.getOptionalFilmById(filmIdInt).orElseThrow(
                () -> new NotFoundException("Film not found by id: " + filmIdInt));
        User user = userStorage.getOptionalUserById(userIdInt).orElseThrow(
                () -> new NotFoundException("User not found by id: " + userIdInt));

        film.deleteLike(user.getId());
        user.deleteLikedFilm(film.getId());
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Film.filmComparatorByLikes)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getOptionalFilmById(id).orElseThrow(
                () -> new NotFoundException("User not found by id: " + id));
    }
}
