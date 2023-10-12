package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class User {
    Integer id;

    public User() {
    }

    @NotBlank(message = "Email should not be null or empty")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Login should not be null or empty")
    String login;

    String name;

    @Past(message = "Birthday should be in past")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    LocalDate birthday;

    public boolean isEmptyName() {
        return name == null || name.isEmpty();
    }

    public void setLoginAsName() {
        name = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday);
    }
}
