package ru.yandex.practicum.filmorate.enumConverter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.SortMode;

@Component
public class SortModeEnumConverter implements Converter<String, SortMode> {
    @Override
    public SortMode convert(String s) {
        return SortMode.valueOf(s);
    }
}
