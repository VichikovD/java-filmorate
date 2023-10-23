package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Optional;
import java.util.Set;

public interface MpaDao {
    public Optional<Mpa> getById(int mpaId);

    public Set<Mpa> getAll();
}
