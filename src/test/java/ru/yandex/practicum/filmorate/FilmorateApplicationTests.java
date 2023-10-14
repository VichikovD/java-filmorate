package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final FilmService filmService;
    private final UserDao userDao;
    private final FilmDao filmDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    //  т.к. теперь FilmDao не отвечает за получение жанров, их наличие не тестируется;

    @Test
    @DisplayName("Create film and compare: 1) created (given to method) and actually inserted films; " +
            "2) returned and inserted. (Only one id test due to continuous id incrementation")
    public void testCreateFilm() {
        Film createdFilm = getFilm(0);

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
        Film filmToUpdate = getFilm(1);
        Film returnedFilmAfterCreation = filmDao.create(getFilm(0));
        filmToUpdate.setId(returnedFilmAfterCreation.getId());

        filmDao.update(filmToUpdate);
        Film insertedFilm = filmDao
                .getById(filmToUpdate.getId())
                .get();

        assertEquals(filmToUpdate, insertedFilm);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = filmDao.create(getFilm(0));
        Film film2 = filmDao.create(getFilm(1));

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
        Film returnedFilm = filmDao.create(getFilm(0));

        Optional<Film> filmOptional = filmDao.getById(returnedFilm.getId());

        assertEquals(Optional.of(returnedFilm), filmOptional);
    }

    @Test
    @DisplayName("Check for 0 likes if new film, check 1 like if add like, check 0 likes if delete like")
    public void testAddAndDeleteLike() {
        Film returnedFilm = filmDao.create(getFilm(0));
        User returnedUser = userDao.create(getUser(0));

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
        Film film1 = filmDao.create(getFilm(0));
        Film film2 = filmDao.create(getFilm(0));
        List<Film> popularFilms = filmDao.getMostPopular(10);
        User user = userDao.create(getUser(0));

        assertEquals(List.of(film1, film2), popularFilms);

        filmDao.addLike(film2, user);
        List<Film> likedFilmsToTop = filmDao.getMostPopular(10);
        Film film2WithLike = filmDao.getById(film2.getId()).get();

        assertEquals(List.of(film2WithLike, film1), likedFilmsToTop);
    }


    @Test
    @DisplayName("when add likes to added films = return films likes-ordered")
    public void testCreateUser() {
        User createdUser = getUser(0);

        User returnedUser = userDao.create(createdUser);
        createdUser.setId(returnedUser.getId());
        User insertedUser = userDao
                .getById(returnedUser.getId())
                .get();

        assertEquals(createdUser, insertedUser);
        assertEquals(createdUser, returnedUser);
    }

    @Test
    public void testUpdateUser() {
        User userToUpdate = getUser(1);
        User returnedUserAfterCreation = userDao.create(getUser(0));
        userToUpdate.setId(returnedUserAfterCreation.getId());

        userDao.update(userToUpdate);
        User insertedUpdateUser = userDao
                .getById(userToUpdate.getId())
                .get();

        assertEquals(userToUpdate, insertedUpdateUser);
        assertEquals(userToUpdate, userToUpdate);
    }

    @Test
    public void testFindUserById() {
        User returnedUser = userDao.create(getUser(0));

        Optional<User> userOptional = userDao.getById(returnedUser.getId());

        assertEquals(Optional.of(returnedUser), userOptional);
    }

    @Test
    public void testGetAllUsersEmpty() {
        List<User> allUsers = userDao.getAll();

        assertEquals(new ArrayList<>(), allUsers);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = userDao.create(getUser(0));
        User user2 = userDao.create(getUser(1));
        List<User> allUsers = userDao.getAll();

        assertEquals(List.of(user1, user2), allUsers);
    }

    @Test
    @DisplayName("no add friend = empty list; user1 add user2 as friend = user1 has user2 as friend, but user2 don't; " +
            "delete friend = empty friend list")
    public void testGetFriendsUsersListById() {
        User user1 = userDao.create(getUser(0));
        User user2 = userDao.create(getUser(1));
        List<User> user1NoFriends = userDao.getFriendsListById(user1.getId());
        userDao.addFriend(user1, user2);
        List<User> user1HasFriendUser2 = userDao.getFriendsListById(user1.getId());
        List<User> user2NoFriends = userDao.getFriendsListById(user2.getId());

        assertEquals(new ArrayList<User>(), user1NoFriends);
        assertEquals(List.of(user2), user1HasFriendUser2);
        assertEquals(new ArrayList<User>(), user2NoFriends);

        userDao.deleteFriend(user1, user2);
        List<User> user1DeletedFriendUser2 = userDao.getFriendsListById(user1.getId());
        List<User> user2StillNoFriends = userDao.getFriendsListById(user2.getId());

        assertEquals(new ArrayList<User>(), user1DeletedFriendUser2);
        assertEquals(new ArrayList<User>(), user2StillNoFriends);
    }

    // id, email, login, name, birthday
    private User getUser(int userId) {
        List<User> userList = List.of(
                new User(null, "email1", "login1", "name1", LocalDate.of(2001, 1, 1)),
                new User(null, "email2", "login2", "name2", LocalDate.of(2002, 2, 2)),
                new User(null, "email3", "login3", "name3", LocalDate.of(2003, 3, 3)));
        return userList.get(userId);
    }

    // id, name, description, releaseDate, duration, likes, Mpa, set<Genre>
    private Film getFilm(int filmId) {
        List<Film> filmList = List.of(
                new Film(null, "name1", "description1", LocalDate.of(1991, 1, 1), 101, 0, getMpaList().get(0), null),
                new Film(null, "name2", "description2", LocalDate.of(1992, 2, 2), 102, 0, getMpaList().get(1), null),
                new Film(null, "name3", "description3", LocalDate.of(1993, 3, 3), 103, 0, getMpaList().get(2), null));
        return filmList.get(filmId);
    }

    /*private List<Genre> getGenreList() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }*/

    private List<Mpa> getMpaList() {
        return List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }
}