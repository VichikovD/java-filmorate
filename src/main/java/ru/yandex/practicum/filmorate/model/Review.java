package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// Используется @ToString для отслеживания тестов в терминале, @EqualsAndHashCode в тестах, @Getter, @Setter,
// Не используется только @RequiredArgsConstructor, поэтому добавил @AllArgsConstructor
// @Builder использую лоя читаемости в makeFilm()
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Review {
    Integer reviewId;

    @NotBlank(message = "Review content should not be null or empty")
    String content;

    @NotNull(message = "Review type should not be null")
    Boolean isPositive;

    @NotNull(message = "Review film_id should not be null")
    Integer filmId;

    @NotNull(message = "Review user_id should not be null")
    Integer userId;

    Integer useful = 0;
}
