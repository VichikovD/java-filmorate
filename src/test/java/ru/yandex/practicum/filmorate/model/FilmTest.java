package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    static Validator validator;

    @BeforeAll
    static void beforeAll() {
        validator = getValidator();
    }

    static Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }


    @Test
    void validFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(getStandardFilm());
        assertTrue(violations.isEmpty(), "Film validated with some ConstraintViolations when supposed not to");
    }

    @Test
    void invalidFilmName() {
        Film film = getStandardFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Film name can't be null or empty")).findFirst().get(),
                "violations doesn't contains Film name ConstraintViolation");
    }

    @Test
    void invalidFilmDescription() {
        Film film = getStandardFilm();
        film.setDescription("Looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "oooooooooooooooooooooooooooooooooooooooooooooooooooooooong description");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Film description can't have more then 200 symbols")).findFirst().get(),
                "violations doesn't contains Film description length ConstraintViolation");
    }

    @Test
    void invalidFilmReleaseDate() {
        Film film = getStandardFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Release date can't be earlier then the 28th of December 1895")).findFirst().get(),
                "violations doesn't contains Film releaseDate ConstraintViolation");
    }

    @Test
    void invalidFilmDuration() {
        Film film = getStandardFilm();
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Film duration should be positive figure")).findFirst().get(),
                "violations doesn't contains Film duration ConstraintViolation");
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