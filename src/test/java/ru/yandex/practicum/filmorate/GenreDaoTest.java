package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDaoTest {
    private final GenreDao genreDao;
    private final PresetData data;

    @Test
    public void testGetAll() {
        Set<Genre> genresActual = genreDao.getAll();
        Set<Genre> genresExpected = new HashSet<>(data.getGenreList());

        assertEquals(genresExpected, genresActual);
    }

    @Test
    public void testGetById() {
        List<Genre> genres = data.getGenreList();
        Genre genre1 = genreDao.getById(1).get();
        Genre genre2 = genreDao.getById(2).get();
        Genre genre3 = genreDao.getById(3).get();
        Genre genre4 = genreDao.getById(4).get();
        Genre genre5 = genreDao.getById(5).get();
        Genre genre6 = genreDao.getById(6).get();

        assertEquals(genres.get(0), genre1);
        assertEquals(genres.get(1), genre2);
        assertEquals(genres.get(2), genre3);
        assertEquals(genres.get(3), genre4);
        assertEquals(genres.get(4), genre5);
        assertEquals(genres.get(5), genre6);
        assertThrows(NoSuchElementException.class, () -> {
            genreDao.getById(7).get();
        });
    }
}
