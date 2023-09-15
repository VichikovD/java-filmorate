package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm(Film thatFilm);

    public List<Film> getAllFilms();

    public Optional<Film> getOptionalFilmById(Integer id);

    public Film getFilmById(Integer id);
}
