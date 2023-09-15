package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        Film filmToReturn = filmService.createFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        Film filmToReturn = filmService.updateFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Optional<String> id,
                        @PathVariable Optional<String> userId) {
        Integer filmIdInt = getIdByOptionalString(id);
        Integer userIdInt = getIdByOptionalString(userId);
        filmService.addLike(filmIdInt, userIdInt);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFriend(@PathVariable Optional<String> id,
                             @PathVariable Optional<String> userId) {
        Integer filmIdInt = getIdByOptionalString(id);
        Integer userIdInt = getIdByOptionalString(userId);
        filmService.deleteLike(filmIdInt, userIdInt);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam Optional<String> count) {
        log.debug("count: " + count);
        String idString = count.orElse("10");
        try {
            int intCount = Integer.parseInt(idString);
            return filmService.getMostPopularFilms(intCount);
        } catch (NumberFormatException e) {
            throw new InvalidIdException(String.format("Invalid Id: %s.", idString));
        }
    }

    private Integer getIdByOptionalString(Optional<String> optString) {
        String idString = optString.orElseThrow(() -> new InvalidIdException("Not identified Id: " + optString));
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new InvalidIdException(String.format("Invalid Id: %s.", idString));
        }
    }

    @GetMapping("/{id:[0-9]+}")
    public Film getFilmById(@PathVariable("id") Optional<Integer> id) {
        log.debug("id: " + id);
        Integer idInt = id.orElseThrow(() -> new InvalidIdException("Not identified Id: " + id));
        return filmService.getFilmById(idInt);
    }
}
