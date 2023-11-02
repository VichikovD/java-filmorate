package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.List;

@Service
public class DirectorService {

    DirectorDao directorDao;
    ValidateService validateService;

    @Autowired
    public DirectorService(DirectorDao directorDao,
                           ValidateService validateService) {
        this.directorDao = directorDao;
        this.validateService = validateService;
    }

    public Director create(Director director) {
        return directorDao.create(director);
    }

    public Director update(Director director) {
        validateService.validateDirectorId(director);
        int directorId = director.getId();
        directorDao.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));

        directorDao.update(director);
        return director;
    }

    public Director getById(int directorId) {
        return directorDao.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));
    }

    public Director deleteById(int directorId) {
        Director directorToRemove = directorDao.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Director not found by id: " + directorId));
        directorDao.deleteById(directorId);
        return directorToRemove;
    }

    public List<Director> getAll() {
        return new ArrayList<>(directorDao.getAll());
    }
}
