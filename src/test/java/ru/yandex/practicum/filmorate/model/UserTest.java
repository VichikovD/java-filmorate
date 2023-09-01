package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();


    @Test
    void validFilm() {
        Set<ConstraintViolation<User>> violations = validator.validate(getStandardUser());

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUserName() {
        User user = getStandardUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validFilmNameEvenIfBlank() {
        User user = getStandardUser();
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUserEmail() {
        User user = getStandardUser();
        user.setEmail("wrongEmail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidUserEmailBlank() {
        User user = getStandardUser();
        user.setEmail("wrongEmail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidUserBirthday() {
        User user = getStandardUser();
        user.setBirthday(LocalDate.of(2023, 12, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    private User getStandardUser() {
        return User.builder()
                .name("Name")
                .login("Login")
                .birthday(LocalDate.of(2000, 10, 15))
                .email("em@mail.ru")
                .build();
    }
}