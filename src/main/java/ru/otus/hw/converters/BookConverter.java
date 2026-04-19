package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(Book book) {
        var genresString = book.getGenres().stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString);
    }

    public String bookDtoToString(BookDto bookDto) {
        var genreString = bookDto.genres().stream()
                .map(genreConverter::genreDtoToString)
                .map("%s"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDto.id(),
                bookDto.title(),
                authorConverter.authorDtoToString(bookDto.author()),
                genreString);
    }

    public BookDto bookToDto(Book book) {
        var genreDtos = book.getGenres().stream()
                .map(genreConverter::genreToDto)
                .toList();
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToDto(book.getAuthor()),
                genreDtos
        );
    }
}
