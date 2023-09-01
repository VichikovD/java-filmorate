package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmStorage filmStorage = new FilmStorage();

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        Film filmToReturn = filmStorage.createFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        Film filmToReturn = filmStorage.updateFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }
}
