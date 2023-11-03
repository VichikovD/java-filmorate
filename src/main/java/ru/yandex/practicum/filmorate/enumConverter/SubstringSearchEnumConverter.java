package ru.yandex.practicum.filmorate.enumConverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.SubstringSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SubstringSearchEnumConverter implements Converter<String, List<SubstringSearch>> {
    @Override
    public List<SubstringSearch> convert(String source) {
        List<SubstringSearch> filterList = new ArrayList<>();
        String[] filter = source.toLowerCase()
                .split(",");
        for (String toConvert : filter) {
            filterList.add(SubstringSearch.valueOf(toConvert));
        }
        return filterList;

        /*if (filter.length == 1 && Objects.equals(filter[0], "director")) {
            filterList.add(SubstringSearch.director);
            return filterList;
        } else if (filter.length == 1 && Objects.equals(filter[0], "title")) {
            filterList.add(SubstringSearch.title);
            return filterList;
        } else if (filter.length == 2 && (
                (Objects.equals(filter[0], "title") && Objects.equals(filter[1], "director"))
                        || (Objects.equals(filter[0], "director") && Objects.equals(filter[1], "title")))) {
            filterList.add(SubstringSearch.title);
            filterList.add(SubstringSearch.director);
            return filterList;
        }

        throw new ValidateException("Invalid filter: " + source + ". Filter may have the following values: director, title");*/

    }
}
