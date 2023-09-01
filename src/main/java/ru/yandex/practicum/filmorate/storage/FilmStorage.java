package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class FilmStorage {
    Integer idCounter = 0;
    LinkedHashMap<Integer, Film> films;

    public FilmStorage() {
        films = new LinkedHashMap<>();
    }

    private int getNewId() {
        return ++idCounter;
    }

    @PostMapping
    public Film createFilm(Film film) {
        int newId = getNewId();
        film.setId(newId);
        films.put(newId, film);
        return film;
    }

    @PutMapping
    public Film updateFilm(Film thatFilm) {
        Film thisFilm = films.get(thatFilm.getId());
        if (thisFilm == null) {
            throw new NotFoundException("Film not found by ID: " + thatFilm.getId());
        }
        films.put(thatFilm.getId(), thatFilm);
        return thatFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
