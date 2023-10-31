package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Service
public class GenreService {
    GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Set<Genre> getAll() {
        return genreDao.getAll();
    }

    public Genre getById(int genreId) {
        return genreDao.getById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found by id: " + genreId));
    }
}
