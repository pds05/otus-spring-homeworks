package ru.otus.hw.dtos;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

public record BookDto(String id, String title, AuthorDto author, List<GenreDto> genres
) {

    public BookDto() {
        this(null, "Default title", new AuthorDto("0", null), new ArrayList<>()
        );
    }

    public static BookDto fromDomainObject(Book book) {
        return new BookDto(book.getId().toString(),
                book.getTitle(),
                AuthorDto.fromDomainObject(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::fromDomainObject).toList()
        );
    }

    public static Book toDomainObject(BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.title);
        book.setAuthor(new Author(bookDto.author.id(), bookDto.author.fullName()));
        book.setGenres(bookDto.genres.stream().map(genreDto -> new Genre(genreDto.id(), genreDto.name())).toList());
        return book;
    }
}
