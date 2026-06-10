package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.BookDto;

import java.util.Set;

public interface BookServiceReactive {

    Mono<BookDto> findById(String id);

    Flux<BookDto> findAll();

    Mono<BookDto> insert(String title, String authorId, Set<String> genresIds);

    Mono<BookDto> update(String id, String title, String authorId, Set<String> genresIds);

    Mono<Void> deleteById(String id);
}
