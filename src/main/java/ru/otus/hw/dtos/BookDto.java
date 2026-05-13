package ru.otus.hw.dtos;

import ru.otus.hw.models.Book;

import java.util.ArrayList;
import java.util.List;

public record BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres) {

    public BookDto() {
        this(null, "Default title", new AuthorDto(0L, null), new ArrayList<>());
    }

    public static BookDto fromDomainObject(Book book) {
        return new BookDto(book.getId(),
                book.getTitle(),
                AuthorDto.fromDomainObject(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::fromDomainObject)
                        .toList());
    }
}
