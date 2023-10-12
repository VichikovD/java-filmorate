package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import java.util.Objects;

@Builder
public class Genre {
    Integer id;
    String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id) && Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                '}';
    }
}
