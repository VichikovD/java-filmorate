package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {
    void create(Event event);

    List<Event> getByUserId(Integer userId);
}
