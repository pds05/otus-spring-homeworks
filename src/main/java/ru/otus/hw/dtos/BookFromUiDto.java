package ru.otus.hw.dtos;


import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class BookFromUiDto {

    @JsonSetter(nulls = Nulls.SKIP)
    private String id = "0";

    private String title;

    private String authorId;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> genreIds;

    public static Book toDomainObject(BookFromUiDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(new Author(dto.authorId, null));
        book.setGenres(dto.genreIds.stream().map(id -> new Genre(id, null)).toList());
        return book;
    }

    public static BookFromUiDto fromDomainObject(Book book) {
        BookFromUiDto dto = new BookFromUiDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setGenreIds(book.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        return dto;
    }

}
