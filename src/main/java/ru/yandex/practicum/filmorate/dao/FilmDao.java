package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortMode;
import ru.yandex.practicum.filmorate.model.SubstringSearch;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film create(Film film);

    void update(Film film);

    Optional<Film> getById(Integer filmId);

    void deleteById(Integer id);

    List<Film> getAll();

    List<Film> getAllMostPopular(Integer count, Integer genreId, Integer year);

    List<Film> getViaSubstringSearch(String query, List<SubstringSearch> filter);

    List<Film> getByDirectorId(Integer directorId, SortMode sortParam);

    List<Film> getRecommendationsById(int userId);

    List<Film> getCommon(Integer userId, Integer friendId);

    void addLike(Film film, User userId);

    void deleteLike(Film filmId, User userId);
}
