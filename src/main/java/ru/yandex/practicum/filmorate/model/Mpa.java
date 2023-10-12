package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

@Builder
public class Mpa {
    Integer id;
    String name;

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mpa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Mpa{" +
                "id=" + id +
                '}';
    }
}
