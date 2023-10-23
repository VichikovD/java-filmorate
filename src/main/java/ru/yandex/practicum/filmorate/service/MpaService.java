package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Service
public class MpaService {
    MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Set<Mpa> getAllMpa() {
        Set<Mpa> sortedMpa = new TreeSet<Mpa>(Comparator.comparing(Mpa::getId));
        sortedMpa.addAll(mpaDao.getAll());
        return sortedMpa;
    }

    public Mpa getMpaByMpaId(int mpaId) {
        return mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));
    }
}
