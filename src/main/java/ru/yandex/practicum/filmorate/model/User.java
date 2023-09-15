package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;

@Data
@Builder
@AllArgsConstructor
public class User {
    Integer id;
    HashSet<Integer> friends;
    HashSet<Integer> likedFilms;

    public User() {
        this.friends = new HashSet<>();
        this.likedFilms = new HashSet<>();
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

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void deleteFriend(Integer id) {
        friends.remove(id);
    }

    public void addLikedFilm(Integer id) {
        likedFilms.add(id);
    }

    public void deleteLikedFilm(Integer id) {
        likedFilms.remove(id);
    }
}
