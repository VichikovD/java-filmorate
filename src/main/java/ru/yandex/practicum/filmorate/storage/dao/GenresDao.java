package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;


public interface GenresDao {
    void updateGenresToFilm(int filmId, Set<Genre> genres);
    public Genre getGenreByGenreId(int genreId);
    public Set<Genre> getGenresByFilmId(int filmId);
    public void deleteGenresFromFilm(int filmId);
    public Set<Genre> getAllGenres();
}
