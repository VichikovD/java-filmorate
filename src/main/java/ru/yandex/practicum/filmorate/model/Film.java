package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidateReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


// Используется @ToString для отслеживания тестов в терминале, @EqualsAndHashCode в тестах, @Getter, @Setter,
@Data       // Не используется только @RequiredArgsConstructor, поэтому добавил @AllArgsConstructor
@Builder    // @Builder использую лоя читаемости в makeFilm()
public class Film {
    Integer id;

    @NotBlank(message = "Film name can't be null or empty")
    String name;

    @Size(max = 200, message = "Film description can't have more then 200 symbols")
    @NotBlank(message = "Film description should not be null or empty")
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @ValidateReleaseDate
    LocalDate releaseDate;

    @Min(value = 0, message = "Film duration should be positive figure")
    int duration;
    Integer likesQuantity;

    @NotNull(message = "Film mpa can't be null or empty")
    Mpa mpa;
    Set<Genre> genres;

    public Film(Integer id, String name, String description, LocalDate releaseDate, int duration, Integer likesQuantity, Mpa mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesQuantity = likesQuantity;
        this.mpa = mpa;
        this.genres = Objects.requireNonNullElseGet(genres, () -> new TreeSet<>(Comparator.comparing(Genre::getId)));
    }
}
