package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmManager filmManager = new FilmManager();

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film, BindingResult bindingResult) throws ValidateFilmException {
        Film filmToReturn = filmManager.createFilm(film, bindingResult);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) throws ValidateFilmException {
        Film filmToReturn = filmManager.updateFilm(film, bindingResult);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmManager.getAllFilms();
    }
}
