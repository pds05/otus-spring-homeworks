package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

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
        return "Id: %d, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString);
    }

    public String bookDtoToString(BookDto bookDto) {
        var genreIdsString = bookDto.genreIds().stream()
                .map("%d"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, authorId: %d, genreIds: [%s]".formatted(
                bookDto.id(),
                bookDto.title(),
                bookDto.authorId(),
                genreIdsString);
    }

    public BookDto bookToDto(Book book) {
        var genreIds = book.getGenres().stream().map(Genre::getId).toList();
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                genreIds
        );
    }

}
