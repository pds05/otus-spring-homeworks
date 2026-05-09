package ru.otus.hw.dtos;

import java.util.ArrayList;
import java.util.List;

public record BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres) {

    public BookDto() {
        this(null, "Default title", new AuthorDto(0L, null), new ArrayList<>());
    }
}
