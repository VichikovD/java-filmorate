package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;


public interface GenreDao {
    void updateToFilm(Film film);

    void setGenresToAllFilms(Collection<Film> filmCollection);

    public Genre getById(int genreId);

    public Set<Genre> getByFilmId(int filmId);

    public void deleteFromFilm(int filmId);

    public Set<Genre> getAll();
}
