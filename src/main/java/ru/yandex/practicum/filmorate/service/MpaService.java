package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

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
        sortedMpa.addAll(mpaDao.getAllMpa());
        return sortedMpa;
    }

    public Mpa getMpaByMpaId(int mpaId) {
        return mpaDao.getMpaByMpaId(mpaId);
    }
}
