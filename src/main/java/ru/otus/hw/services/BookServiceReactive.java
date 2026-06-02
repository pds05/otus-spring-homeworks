package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.BookDto;


import java.util.Set;

public interface BookServiceReactive extends BookService {

    Mono<BookDto> findByIdReactive(String id);

    Flux<BookDto> findAllReactive();

    Mono<BookDto> insertReactive(String title, String authorId, Set<String> genresIds);

    Mono<BookDto> updateReactive(String id, String title, String authorId, Set<String> genresIds);

    Mono<Void> deleteByIdReactive(String id);
}
