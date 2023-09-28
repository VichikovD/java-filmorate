package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getAllFilms();

    public Optional<Film> getFilmById(Integer filmId);


    public List<Film> getMostPopularFilms(Integer count);

    void addLike(Film film, User userId);

    public void deleteLike(Film filmId, User userId);
}
