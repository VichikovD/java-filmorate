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

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable("id") Integer id) {
        log.info("DELETE \"/films/" + id + "\"");
        filmService.deleteById(id);
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
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") @Min(value = 1) int count,
                                          @RequestParam(required = false) Integer genreId,
                                          @RequestParam(required = false) Integer year) {
        log.info("GET {}, query parameters={}", "\"/films/popular\"", "{count=" + count + ", genre_id=" + genreId + ", year=" + year + "}");
        List<Film> filmsList = filmService.getMostPopularFilms(count, genreId, year);
        log.debug(filmsList.toString());
        return filmsList;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("GET {}, query parameters={}", "\"/films/common\"", "{userId=" + userId + ", friendId=" + friendId + "}");

        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.debug(commonFilms.toString());

        return commonFilms;
    }


    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable int directorId,
                                       @RequestParam(defaultValue = "film_id") String sortBy) {
        log.info("GET {}, query parameters={}", "\"/films/director/" + directorId + "\"", "{sortBy=" + sortBy + "}");
        List<Film> filmsList = filmService.getDirectorFilms(directorId, sortBy);
        log.debug(filmsList.toString());
        return filmsList;
    }
}
