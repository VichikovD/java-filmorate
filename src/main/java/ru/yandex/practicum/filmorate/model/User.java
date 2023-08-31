package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    int id;

    @NotBlank(message = "Email should not be Blank")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Login should not be Blank")
    String login;

    String name;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    LocalDate birthday;
}
