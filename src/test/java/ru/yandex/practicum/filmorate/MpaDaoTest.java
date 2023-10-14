package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

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
public class MpaDaoTest {
    private final PresetData data;
    private final MpaDao mpaDao;

    @Test
    public void testGetAll() {
        Set<Mpa> mpasActual = mpaDao.getAll();
        Set<Mpa> mpasExpected = new HashSet<>(data.getMpaList());

        assertEquals(mpasExpected, mpasActual);
    }

    @Test
    public void testGetById() {
        List<Mpa> mpas = data.getMpaList();
        Mpa Mpa1 = mpaDao.getById(1).get();
        Mpa Mpa2 = mpaDao.getById(2).get();
        Mpa Mpa3 = mpaDao.getById(3).get();
        Mpa Mpa4 = mpaDao.getById(4).get();
        Mpa Mpa5 = mpaDao.getById(5).get();

        assertEquals(mpas.get(0), Mpa1);
        assertEquals(mpas.get(1), Mpa2);
        assertEquals(mpas.get(2), Mpa3);
        assertEquals(mpas.get(3), Mpa4);
        assertEquals(mpas.get(4), Mpa5);
        assertThrows(NoSuchElementException.class, () -> {
            mpaDao.getById(6).get();
        });
    }
}
