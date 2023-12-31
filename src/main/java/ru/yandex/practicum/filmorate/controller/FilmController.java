package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("POST {}, body={}", "\"/films\"", film);
        Film filmToReturn = filmService.createFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("PUT {}, body={}", "\"/films\"", film);
        Film filmToReturn = filmService.updateFilm(film);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") int id) {
        log.info("GET \"/films/" + id + "\"");
        Film filmToReturn = filmService.getFilmById(id);
        log.debug(filmToReturn.toString());
        return filmToReturn;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET \"/films\"");
        List<Film> filmsList = filmService.getAllFilms();
        log.debug(filmsList.toString());
        return filmsList;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                        @PathVariable int userId) {
        log.info("PUT {}", "\"/films/" + id + "/like/" + userId + "\"");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id,
                           @PathVariable int userId) {
        log.info("DELETE {}", "\"/films/" + id + "/like/" + userId + "\"");
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") @Min(value = 1) int count) {
        log.info("GET {}, query parameters={}", "\"/films/popular\"", "{count=" + count + "}");
        List<Film> filmsList = filmService.getMostPopularFilms(count);
        log.debug(filmsList.toString());
        return filmsList;
    }
}
