package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDaoTest {
    private final PresetData data;
    private final UserDao userDao;


    @Test
    @DisplayName("when add likes to added films = return films likes-ordered")
    public void testCreateUser() {
        User createdUser = data.getUser(0);

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
        User userToUpdate = data.getUser(1);
        User returnedUserAfterCreation = userDao.create(data.getUser(0));
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
        User returnedUser = userDao.create(data.getUser(0));

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
        User user1 = userDao.create(data.getUser(0));
        User user2 = userDao.create(data.getUser(1));
        List<User> allUsers = userDao.getAll();

        assertEquals(List.of(user1, user2), allUsers);
    }

    @Test
    @DisplayName("no add friend = empty list; user1 add user2 as friend = user1 has user2 as friend, but user2 don't; " +
            "delete friend = empty friend list")
    public void testGetFriendsUsersListById() {
        User user1 = userDao.create(data.getUser(0));
        User user2 = userDao.create(data.getUser(1));
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
}
