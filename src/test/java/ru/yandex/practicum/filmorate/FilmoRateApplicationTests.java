package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.GenresDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.dao.daoImpl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.daoImpl.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenresDao genresDao;
    private final MpaDao mpaDao;


    @Test
    @DisplayName("Create film and compare: 1) created (given to method) and actually inserted films; " +
            "2) returned and inserted. (Only one id test due to continuous id incrementation")
    public void testCreateFilm() {
        Film createdFilm = getFilm(0);

        Film returnedFilm = filmDbStorage.createFilm(createdFilm);
        createdFilm.setId(returnedFilm.getId());
        Film insertedFilm = filmDbStorage
                .getFilmById(returnedFilm.getId())
                .get();

        assertEquals(createdFilm, insertedFilm);
        assertEquals(createdFilm, returnedFilm);
    }

    @Test
    public void testUpdateFilm() {
        Film filmToUpdate = getFilm(1);
        Film returnedFilmAfterCreation = filmDbStorage.createFilm(getFilm(0));
        filmToUpdate.setId(returnedFilmAfterCreation.getId());

        Film returnedFilm = filmDbStorage.updateFilm(filmToUpdate);
        Film insertedFilm = filmDbStorage
                .getFilmById(filmToUpdate.getId())
                .get();

        assertEquals(filmToUpdate, insertedFilm);
        assertEquals(filmToUpdate, returnedFilm);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = filmDbStorage.createFilm(getFilm(0));
        Film film2 = filmDbStorage.createFilm(getFilm(1));

        List<Film> allFilms = filmDbStorage.getAllFilms();

        assertEquals(List.of(film1, film2), allFilms);
    }

    @Test
    public void testGetAllFilmsEmpty() {
        List<Film> allFilms = filmDbStorage.getAllFilms();

        assertEquals(new ArrayList<>(), allFilms);
    }

    @Test
    public void testFindFilmById() {
        Film returnedFilm = filmDbStorage.createFilm(getFilm(0));

        Optional<Film> filmOptional = filmDbStorage.getFilmById(returnedFilm.getId());

        assertEquals(Optional.of(returnedFilm), filmOptional);
    }

    @Test
    @DisplayName("Check for 0 likes if new film, check 1 like if add like, check 0 likes if delete like")
    public void testAddAndDeleteLike() {
        Film returnedFilm = filmDbStorage.createFilm(getFilm(0));
        User returnedUser = userStorage.createUser(getUser(0));

        assertEquals(0, returnedFilm.getLikesQuantity());

        filmDbStorage.addLike(returnedFilm, returnedUser);
        Film filmWithLike = filmDbStorage.getFilmById(returnedFilm.getId()).get();

        assertEquals(1, filmWithLike.getLikesQuantity());

        filmDbStorage.deleteLike(returnedFilm, returnedUser);
        Film filmWithDeletedLike = filmDbStorage.getFilmById(returnedFilm.getId()).get();

        assertEquals(0, filmWithDeletedLike.getLikesQuantity());
    }

    @Test
    @DisplayName("when add likes to added films = return films likes-ordered")
    public void testGetMostPopularFilms() {
        Film returnedFilmNoLikes = filmDbStorage.createFilm(getFilm(0));
        List<Film> popularFilms = filmDbStorage.getMostPopularFilms(10);
        User user = userStorage.createUser(getUser(0));

        assertNotEquals(returnedFilmNoLikes, popularFilms.get(0));

        filmDbStorage.addLike(returnedFilmNoLikes, user);
        List<Film> likedFilmsToTopList = filmDbStorage.getMostPopularFilms(10);
        Film returnedFilmWithLike = filmDbStorage.getFilmById(returnedFilmNoLikes.getId()).get();

        assertEquals(returnedFilmWithLike, likedFilmsToTopList.get(0));
    }

    @Test
    @DisplayName("when add likes to added films = return films likes-ordered")
    public void testCreateUser() {
        User createdUser = getUser(0);

        User returnedUser = userStorage.createUser(createdUser);
        createdUser.setId(returnedUser.getId());
        User insertedUser = userStorage
                .getUserById(returnedUser.getId())
                .get();

        assertEquals(createdUser, insertedUser);
        assertEquals(createdUser, returnedUser);
    }

    @Test
    public void testUpdateUser() {
        User userToUpdate = getUser(1);
        User returnedUserAfterCreation = userStorage.createUser(getUser(0));
        userToUpdate.setId(returnedUserAfterCreation.getId());

        User returnedUpdateUser = userStorage.updateUser(userToUpdate);
        User insertedUpdateUser = userStorage
                .getUserById(userToUpdate.getId())
                .get();

        assertEquals(userToUpdate, insertedUpdateUser);
        assertEquals(userToUpdate, returnedUpdateUser);
    }

    @Test
    public void testFindUserById() {
        User returnedUser = userStorage.createUser(getUser(0));

        Optional<User> userOptional = userStorage.getUserById(returnedUser.getId());

        assertEquals(Optional.of(returnedUser), userOptional);
    }

    @Test
    @DisplayName("no add friend = empty list; user1 add user2 as friend = user1 has user2 as friend, but user2 don't; " +
            "delete friend = empty friend list")
    public void testGetFriendsUsersListById() {
        User user1 = userStorage.createUser(getUser(0));
        User user2 = userStorage.createUser(getUser(1));
        List<User> user1NoFriends = userStorage.getFriendsUsersListById(user1.getId());
        userStorage.addFriend(user1, user2);
        List<User> user1HasFriendUser2 = userStorage.getFriendsUsersListById(user1.getId());
        List<User> user2NoFriends = userStorage.getFriendsUsersListById(user2.getId());

        assertEquals(new ArrayList<User>(), user1NoFriends);
        assertEquals(List.of(user2), user1HasFriendUser2);
        assertEquals(new ArrayList<User>(), user2NoFriends);

        userStorage.deleteFriend(user1, user2);
        List<User> user1DeletedFriendUser2 = userStorage.getFriendsUsersListById(user1.getId());
        List<User> user2StillNoFriends = userStorage.getFriendsUsersListById(user2.getId());

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
                new Film(null, "name1", "description1", LocalDate.of(1991, 1, 1), 101, 0, getMpaList().get(0), new HashSet<>()),
                new Film(null, "name2", "description2", LocalDate.of(1992, 2, 2), 102, 0, getMpaList().get(1), Set.of(getGenreList().get(0))),
                new Film(null, "name3", "description3", LocalDate.of(1993, 3, 3), 103, 0, getMpaList().get(2), Set.of(getGenreList().get(1), getGenreList().get(2))));
        return filmList.get(filmId);
    }

    private List<Genre> getGenreList() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));
    }

    private List<Mpa> getMpaList() {
        return List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));
    }
}