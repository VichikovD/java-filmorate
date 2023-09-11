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

class UserTest {
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
    void validUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(getStandardUser());

        assertTrue(violations.isEmpty(), "User validated with some ConstraintViolations when supposed not to");
    }

    @Test
    void validFilmNameEvenIfBlank() {
        User user = getStandardUser();
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "User validated with some ConstraintViolations when supposed not to");
    }

    @Test
    void invalidUserEmail() {
        User user = getStandardUser();
        user.setEmail("wrongEmail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Email should be valid")).findFirst().get(),
                "violations doesn't contains User email ConstraintViolation");
    }

    @Test
    void invalidUserEmailBlank() {
        User user = getStandardUser();
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Email should not be null or empty")).findFirst().get(),
                "violations doesn't contains User email ConstraintViolation");
    }

    @Test
    void invalidUserLoginBlank() {
        User user = getStandardUser();
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Login should not be null or empty")).findFirst().get(),
                "violations doesn't contains User login ConstraintViolation");
    }

    @Test
    void invalidUserBirthday() {
        User user = getStandardUser();
        user.setBirthday(LocalDate.of(2023, 12, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertDoesNotThrow(() -> violations.stream().filter(cViolation -> cViolation.getMessage()
                        .equals("Birthday should be in past")).findFirst().get(),
                "violations doesn't contains User birthday ConstraintViolation");
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