package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresDao;

import java.util.Set;

@Service
public class GenreService {
    GenresDao genresDao;

    @Autowired
    public GenreService(GenresDao genresDao) {
        this.genresDao = genresDao;
    }

    public Set<Genre> getAllGenres() {
        return genresDao.getAllGenres();
    }

    public Genre getGenreByGenreId(int genreId) {
        return genresDao.getGenreByGenreId(genreId);
    }
}
