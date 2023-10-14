package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;
import java.util.Set;


public interface GenreDao {
    public Optional<Genre> getById(int genreId);

    public Set<Genre> getAll();
}
