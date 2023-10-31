package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.constraints.Min;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/genres")
@Validated
public class GenreController {
    GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Set<Genre> getAll() {
        log.info("GET \"/films/genres\"");
        Set<Genre> genreList = genreService.getAll();
        log.debug(genreList.toString());
        return genreList;
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable @Min(value = 1) int id) {
        log.info("GET \"/genres/" + id + "\"");
        Genre genre = genreService.getById(id);
        log.debug(genre.toString());
        return genre;
    }
}
