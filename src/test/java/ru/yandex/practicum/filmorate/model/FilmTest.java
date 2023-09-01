package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();


    @Test
    void validFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(getStandardFilm());
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidFilmName() {
        Film film = getStandardFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidFilmDescription() {
        Film film = getStandardFilm();
        film.setDescription("Looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "oooooooooooooooooooooooooooooooooooooooooooooooooooooooong description");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidFilmReleaseDate() {
        Film film = getStandardFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidFilmDuration() {
        Film film = getStandardFilm();
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    private Film getStandardFilm() {
        return Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 10, 15))
                .duration(100)
                .build();
    }
}