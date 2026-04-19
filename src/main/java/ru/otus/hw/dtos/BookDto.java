package ru.otus.hw.dtos;

import java.util.List;

public record BookDto(String id, String title, AuthorDto author, List<GenreDto> genres) {
}
