package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class FilmManager {
    HashMap<Integer, Film> films;

    public FilmManager() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film createFilm(Film film, BindingResult bindingResult) throws ValidateFilmException {
        handleValidateErrors(bindingResult);
        validateReleaseDateFilm(film);
        int newId = films.size() + 1;
        film.setId(newId);
        films.put(newId, film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film thatFilm, BindingResult bindingResult) throws ValidateFilmException {
        handleValidateErrors(bindingResult);
        validateReleaseDateFilm(thatFilm);
        Film thisFilm = films.get(thatFilm.getId());
        if (thisFilm == null) {
            throw new ValidateFilmException("Film not found by ID: " + thatFilm.getId());
        }
        films.put(thatFilm.getId(), thatFilm);
        return thatFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateReleaseDateFilm(Film film) throws ValidateFilmException {
        String loggerMessage = "Film is not valid";
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String exceptionMessage = "Film release date can't be earlier than the 28th of December 1895";
            log.debug("{} - {}", loggerMessage, exceptionMessage);
            throw new ValidateFilmException(exceptionMessage);
        }
    }
    private void handleValidateErrors(BindingResult bindingResult) throws ValidateFilmException {
        if (bindingResult.hasErrors()) {
            List<String> errorsList = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                String errorMessage = error.getDefaultMessage();
                log.debug(errorMessage);
                errorsList.add(errorMessage);
            }
            throw new ValidateFilmException(String.join(", ", errorsList));
        }
    }
}
