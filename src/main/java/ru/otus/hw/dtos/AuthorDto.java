package ru.otus.hw.dtos;

import ru.otus.hw.models.Author;

public record AuthorDto(Long id, String fullName) {

    public static AuthorDto fromDomainObject(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
