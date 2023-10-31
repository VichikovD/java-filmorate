package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    Film create(Film film);

    void update(Film film);

    List<Film> getAll();

    List<Film> getFilmsViaSubstringSearch(HashMap<String, String> searchFilter);

    Optional<Film> getById(Integer filmId);

    List<Film> getByIds(List<Integer> filmsIds);

    void deleteById(Integer id);

    List<Film> getCommon(Integer userId, Integer friendId);

    List<Film> getMostPopular(Integer count, Integer genreId, Integer year);

    void addLike(Film film, User userId);

    void deleteLike(Film filmId, User userId);

    List<Film> getByDirectorId(Integer directorId, String sortParam);

    public void updateGenresToAllFilms(Collection<Film> filmCollection);

    public void updateDirectorsToAllFilms(Collection<Film> filmCollection);
}
