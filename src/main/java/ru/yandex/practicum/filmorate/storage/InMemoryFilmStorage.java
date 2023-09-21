package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Integer counterId = 0;
    private LinkedHashMap<Integer, Film> films;

    private HashMap<Integer, HashSet<Integer>> likes;

    public Comparator<Film> filmComparatorByLikes = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikesQuantity() - o1.getLikesQuantity();
        }
    };

    public List<Film> getMostPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted(filmComparatorByLikes)
                .limit(count)
                .collect(Collectors.toList());
    }

    public InMemoryFilmStorage() {
        this.films = new LinkedHashMap<>();
        this.likes = new HashMap<>();
    }

    public Integer getLikesQuantity(int filmId) {
        return likes.get(filmId)
                .size();
    }

    public void addLike(Film film, User user) {
        film.addLike();
        likes.get(film.getId())
                .add(user.getId());
    }

    public void deleteLike(Film film, User user) {
        film.deleteLike();
        likes.get(film.getId())
                .remove(user.getId());
    }

    private Integer getNewId() {
        return ++counterId;
    }

    @Override
    public Film createFilm(Film film) {
        Integer newId = getNewId();
        film.setId(newId);
        films.put(newId, film);
        likes.put(newId, new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        Film oldFilm = films.get(filmId);
        film.setLikesQuantity(oldFilm.getLikesQuantity());
        films.put(filmId, film);
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getFilmById(Integer filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

}
