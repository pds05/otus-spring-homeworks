package ru.otus.hw.dtos;

import ru.otus.hw.models.Genre;

public record GenreDto(Long id, String name) {

    public static GenreDto fromDomainObject(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
