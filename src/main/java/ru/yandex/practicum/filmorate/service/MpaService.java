package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Set;

@Service
public class MpaService {
    MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Set<Mpa> getAll() {
        return mpaDao.getAll();
    }

    public Mpa getById(int mpaId) {
        return mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));
    }
}
