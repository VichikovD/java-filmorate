package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@Validated
public class DirectorController {

    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("POST {}, body={}", "\"/directors\"", director);
        Director directorToReturn = directorService.createDirector(director);
        log.debug(directorToReturn.toString());
        return directorToReturn;
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("PUT {}, body={}", "\"/directors\"", director);
        Director directorToReturn = directorService.updateDirector(director);
        log.debug(directorToReturn.toString());
        return directorToReturn;
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("GET {}", "\"/directors/" + id + "\"");
        Director directorToReturn = directorService.getDirectorById(id);
        log.debug(directorToReturn.toString());
        return directorToReturn;
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("GET {}", "\"/directors\"");
        List<Director> directorList = directorService.getAllDirectors();
        log.debug(directorList.toString());
        return directorList;
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        log.info("GET {}", "\"/directors/" + id + "\"");
        Director directorToReturn = directorService.deleteDirectorById(id);
        log.debug(directorToReturn.toString());
    }
}
