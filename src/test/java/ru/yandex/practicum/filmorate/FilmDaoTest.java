package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDaoTest {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final PresetData data;

    @Test
    @DisplayName("Create film and compare: 1) created (given to method) and actually inserted films; " +
            "2) returned and inserted. (Only one id test due to continuous id incrementation")
    public void testCreateFilm() {
        Film createdFilm = data.getFilm(0);

        Film returnedFilm = filmDao.create(createdFilm);
        createdFilm.setId(returnedFilm.getId());
        Film insertedFilm = filmDao
                .getById(returnedFilm.getId())
                .get();

        assertEquals(createdFilm, insertedFilm);
        assertEquals(createdFilm, returnedFilm);
    }

    @Test
    public void testUpdateFilm() {
        Film filmToUpdate = data.getFilm(1);
        Film returnedFilmAfterCreation = filmDao.create(data.getFilm(0));
        filmToUpdate.setId(returnedFilmAfterCreation.getId());

        filmDao.update(filmToUpdate);
        Film insertedFilm = filmDao
                .getById(filmToUpdate.getId())
                .get();

        assertEquals(filmToUpdate, insertedFilm);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = filmDao.create(data.getFilm(0));
        Film film2 = filmDao.create(data.getFilm(1));

        List<Film> allFilms = filmDao.getAll();

        assertEquals(List.of(film1, film2), allFilms);
    }

    @Test
    public void testGetAllFilmsEmpty() {
        List<Film> allFilms = filmDao.getAll();

        assertEquals(new ArrayList<>(), allFilms);
    }

    @Test
    public void testFindFilmById() {
        Film returnedFilm = filmDao.create(data.getFilm(0));

        Optional<Film> filmOptional = filmDao.getById(returnedFilm.getId());

        assertEquals(Optional.of(returnedFilm), filmOptional);
    }

    @Test
    @DisplayName("Check for 0 likes if new film, check 1 like if add like, check 0 likes if delete like")
    public void testAddAndDeleteLike() {
        Film returnedFilm = filmDao.create(data.getFilm(0));
        User returnedUser = userDao.create(data.getUser(0));

        assertEquals(0, returnedFilm.getLikesQuantity());

        filmDao.addLike(returnedFilm, returnedUser);
        Film filmWithLike = filmDao.getById(returnedFilm.getId()).get();

        assertEquals(1, filmWithLike.getLikesQuantity());

        filmDao.deleteLike(returnedFilm, returnedUser);
        Film filmWithDeletedLike = filmDao.getById(returnedFilm.getId()).get();

        assertEquals(0, filmWithDeletedLike.getLikesQuantity());
    }

    @Test
    @DisplayName("when add likes to added films = return films likes-ordered")
    public void testGetMostPopularFilms() {
        Film film1 = filmDao.create(data.getFilm(0));
        Film film2 = filmDao.create(data.getFilm(0));
        List<Film> popularFilms = filmDao.getMostPopular(10);
        User user = userDao.create(data.getUser(0));

        assertEquals(List.of(film1, film2), popularFilms);

        filmDao.addLike(film2, user);
        List<Film> likedFilmsToTop = filmDao.getMostPopular(10);
        Film film2WithLike = filmDao.getById(film2.getId()).get();

        assertEquals(List.of(film2WithLike, film1), likedFilmsToTop);
    }
}
