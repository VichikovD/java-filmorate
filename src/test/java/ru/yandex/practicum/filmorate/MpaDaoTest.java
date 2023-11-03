package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

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
        List<Mpa> mpasActual = mpaDao.getAll();
        List<Mpa> mpasExpected = new ArrayList<>(data.getMpaList());

        assertEquals(mpasExpected, mpasActual);
    }

    @Test
    public void testGetById() {
        List<Mpa> mpas = data.getMpaList();
        Mpa mpa1 = mpaDao.getById(1).get();
        Mpa mpa2 = mpaDao.getById(2).get();
        Mpa mpa3 = mpaDao.getById(3).get();
        Mpa mpa4 = mpaDao.getById(4).get();
        Mpa mpa5 = mpaDao.getById(5).get();

        assertEquals(mpas.get(0), mpa1);
        assertEquals(mpas.get(1), mpa2);
        assertEquals(mpas.get(2), mpa3);
        assertEquals(mpas.get(3), mpa4);
        assertEquals(mpas.get(4), mpa5);
        assertThrows(NoSuchElementException.class, () -> {
            mpaDao.getById(6).get();
        });
    }
}
