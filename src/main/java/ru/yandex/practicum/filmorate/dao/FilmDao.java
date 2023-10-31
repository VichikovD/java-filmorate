package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film create(Film film);

    void update(Film film);

    List<Film> getAll();

    List<Film> getFilmsViaSubstringSearch(HashMap<String, String> searchFilter);

    Optional<Film> getById(Integer filmId);

    void deleteById(Integer id);

    List<Film> getCommon(Integer userId, Integer friendId);

    public List<Film> getMostPopular(Integer count);

    void addLike(Film film, User userId);

    void deleteLike(Film filmId, User userId);

    List<Film> getByDirectorId(Integer directorId, String sortParam);
}
