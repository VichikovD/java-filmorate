package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import java.util.Objects;

@Builder
public class Mpa {
    Integer id;
    String name;

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Mpa() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpa mpa = (Mpa) o;
        return Objects.equals(id, mpa.id) && Objects.equals(name, mpa.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Mpa{" +
                "id=" + id +
                '}';
    }
}
