package ru.otus.hw.converters;

import org.springframework.core.convert.converter.Converter;
import ru.otus.hw.dtos.GenreDto;

public class StringToGenreConverter implements Converter<String, GenreDto> {
    @Override
    public GenreDto convert(String source) {
        String[] propArr = source.substring(source.indexOf('[') + 1, source.indexOf(']')).split(", ");
        long id = Long.parseLong(propArr[0].split("=")[1]);
        String name = propArr[1].split("=")[1];
        return new GenreDto(id, name);
    }
}
