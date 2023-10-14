package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Service
public class GenreService {
    GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Set<Genre> getAllGenres() {
        return genreDao.getAll();
    }

    public Genre getGenreByGenreId(int genreId) {
        // Добавить проверку полученного резулитата и выбросить NFE если не найден
        return genreDao.getById(genreId);
    }
}
