package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidateReleaseDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate dateOfFirstFilmEver = LocalDate.of(1895, 12, 28);
        return !releaseDate.isBefore(dateOfFirstFilmEver);
    }
}
