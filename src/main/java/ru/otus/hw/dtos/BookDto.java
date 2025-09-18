package ru.otus.hw.dtos;

import java.util.List;

public record BookDto(long id, String title, long authorId, List<Long> genreIds) {
}
