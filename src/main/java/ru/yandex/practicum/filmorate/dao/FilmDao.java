package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    public Film create(Film film);

    public void update(Film film);

    public List<Film> getAll();

    public Optional<Film> getById(Integer filmId);


    List<Film> getMostPopular(Integer count, Integer genreId, Integer year);

    void addLike(Film film, User userId);

    public void deleteLike(Film filmId, User userId);
}
