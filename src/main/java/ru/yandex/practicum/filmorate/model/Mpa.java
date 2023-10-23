package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Mpa {
    Integer id;
    String name;

    public Mpa(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpa mpa = (Mpa) o;
        return Objects.equals(id, mpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
