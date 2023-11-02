package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {

    Director create(Director director);

    void update(Director director);

    Optional<Director> getById(Integer id);

    void deleteById(Integer id);

    List<Director> getAll();
}
