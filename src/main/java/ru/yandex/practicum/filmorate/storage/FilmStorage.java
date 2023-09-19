package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public Film createFilm(Film film);

    public void updateFilm(Film thatFilm);

    public List<Film> getAllFilms();

    public Optional<Film> getFilmById(Integer id);


    public List<Film> getMostPopularFilms(Integer count);

    void addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);
}
