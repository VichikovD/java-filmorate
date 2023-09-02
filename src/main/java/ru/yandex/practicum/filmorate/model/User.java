package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    Integer id;

    @NotBlank(message = "Email should not be null or empty")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Login should not be null or empty")
    String login;

    String name;

    @Past(message = "Birthday should be in past")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    LocalDate birthday;
}
