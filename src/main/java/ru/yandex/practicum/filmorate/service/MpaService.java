package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Service
public class MpaService {
    MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public List<Mpa> getAllMpa() {
        return mpaDao.getAll();
    }

    public Mpa getMpaByMpaId(int mpaId) {
        return mpaDao.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa not found by id: " + mpaId));
    }
}
