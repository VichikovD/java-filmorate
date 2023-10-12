package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

@Builder
public class Genre {
    Integer id;
    String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                '}';
    }
}
