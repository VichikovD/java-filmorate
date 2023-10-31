package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortMode;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film create(Film film);

    void update(Film film);

    Optional<Film> getById(Integer filmId);

    void deleteById(Integer id);

    List<Film> getAll();

    List<Film> getAllMostPopular(Integer count, Integer genreId, Integer year);

    List<Film> getViaSubstringSearch(HashMap<String, String> searchFilter);

    List<Film> getByDirectorId(Integer directorId, String sortParam);

    List<Film> getCommon(Integer userId, Integer friendId);

    void addLike(Film film, User userId);

    void deleteLike(Film filmId, User userId);

    void updateGenresToAllFilms(Collection<Film> filmCollection);

    void updateDirectorsToAllFilms(Collection<Film> filmCollection);
}
