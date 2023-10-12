package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Set;

public interface MpaDao {
    public Mpa getMpaByMpaId(int mpaId);

    public Set<Mpa> getAllMpa();
}
