package ru.yandex.practicum.filmorate.enumConverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.SubstringSearch;

import java.util.Objects;

@Component
public class SubstringSearchEnumConverter implements Converter<String, SubstringSearch> {
    @Override
    public SubstringSearch convert(String source) {
        String[] filter = source.toUpperCase()
                .replaceAll(" ", "")
                .split(",");
        if (filter.length == 1 && Objects.equals(filter[0], "DIRECTOR")) {
            return SubstringSearch.DIRECTOR;
        } else if (filter.length == 1 && Objects.equals(filter[0], "TITLE")) {
            return SubstringSearch.TITLE;
        } else if (filter.length == 2 && (
                (Objects.equals(filter[0], "TITLE") && Objects.equals(filter[1], "DIRECTOR"))
                        || (Objects.equals(filter[0], "DIRECTOR") && Objects.equals(filter[1], "TITLE")))) {
            return SubstringSearch.DIRECTOR_TITLE;
        } else {
            throw new ValidateException("Invalid filter: " + source + ". Filter may have the following values: director, title");
        }
    }
}
