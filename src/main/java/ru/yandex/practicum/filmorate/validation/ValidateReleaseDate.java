package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ValidateReleaseDate {
    String message() default "Release date can't be earlier then the 28th of December 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
