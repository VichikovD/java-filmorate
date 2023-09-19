package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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
            return getLikesQuantity(o2.getId()).compareTo(getLikesQuantity(o1.getId()));
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

    public Integer getLikesQuantity(int id) {
        return likes.get(id)
                .size();
    }

    public void addLike(Integer filmId, Integer userId) {
        likes.get(filmId)
                .add(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likes.get(filmId)
                .remove(userId);
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
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

}
