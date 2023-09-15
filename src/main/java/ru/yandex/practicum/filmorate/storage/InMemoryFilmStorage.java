package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Integer counterId = 0;
    private LinkedHashMap<Integer, Film> films;

    public InMemoryFilmStorage() {
        films = new LinkedHashMap<>();
    }

    private Integer getNewId() {
        return ++counterId;
    }

    @Override
    public Film createFilm(Film film) {
        Integer newId = getNewId();
        film.setId(newId);
        films.put(newId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film thatFilm) {
        Film thisFilm = films.get(thatFilm.getId());
        if (thisFilm == null) {
            throw new NotFoundException("Film not found by ID: " + thatFilm.getId());
        }
        films.put(thatFilm.getId(), thatFilm);
        return thatFilm;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getOptionalFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    public Film getFilmById(Integer id) {
        return films.get(id);
    }
}
