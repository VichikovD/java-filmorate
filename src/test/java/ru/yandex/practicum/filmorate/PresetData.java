package ru.yandex.practicum.filmorate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
public class PresetData {
    public User getUser(int userId) {
        List<User> userList = List.of(
                new User(null, "email1", "login1", "name1", LocalDate.of(2001, 1, 1)),
                new User(null, "email2", "login2", "name2", LocalDate.of(2002, 2, 2)),
                new User(null, "email3", "login3", "name3", LocalDate.of(2003, 3, 3)));
        return userList.get(userId);
    }

    // id, name, description, releaseDate, duration, likes, Mpa, set<Genre>
    public Film getFilm(int filmId) {
        List<Film> filmList = List.of(
                new Film(null, "name1", "description1", LocalDate.of(1991, 1, 1), 101, 0, getMpaList().get(0), new HashSet<>(), new HashSet<>()),
                new Film(null, "name2", "description2", LocalDate.of(1992, 2, 2), 102, 0, getMpaList().get(1), new HashSet<>(), new HashSet<>()),
                new Film(null, "name3", "description3", LocalDate.of(1993, 3, 3), 103, 0, getMpaList().get(2), new HashSet<>(), new HashSet<>()));
        return filmList.get(filmId);
    }

    public List<Genre> getGenreList() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    public List<Mpa> getMpaList() {
        return List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }
}
