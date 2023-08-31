package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {
    int id;

    @NotBlank(message = "Film name can't be empty")
    String name;

    @Size(max = 200, message = "Film description can't have more then 200 symbols")
    @NotBlank(message = "Film description should not be blank")
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    LocalDate releaseDate;

    @Min(value = 0, message = "Film duration should be positive figure")
    int duration;
}
