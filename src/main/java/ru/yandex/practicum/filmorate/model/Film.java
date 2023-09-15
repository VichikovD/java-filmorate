package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidateReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;

@Data
@Builder
@AllArgsConstructor
public class Film {
    public static Comparator<Film> filmComparatorByLikes = new Comparator<Film>() {
        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikesQuantity().compareTo(o1.getLikesQuantity());
        }
    };

    Integer id;
    HashSet<Integer> likes;

    public Film() {
        this.likes = new HashSet<>();
    }

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

    public Integer getLikesQuantity() {
        return likes.size();
    }

    public void addLike(Integer id) {
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        likes.remove(id);
    }

}
