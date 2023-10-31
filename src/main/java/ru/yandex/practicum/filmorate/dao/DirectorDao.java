package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Optional;
import java.util.Set;

public interface DirectorDao {

    Director create(Director director);

    void update(Director director);

    Optional<Director> getById(Integer id);

    void deleteById(Integer id);

    Set<Director> getAll();
}
