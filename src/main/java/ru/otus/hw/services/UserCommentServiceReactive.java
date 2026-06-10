package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.UserCommentDto;

public interface UserCommentServiceReactive {

    Mono<UserCommentDto> findById(String id);

    Flux<UserCommentDto> findAllByBookId(String bookId);

    Mono<UserCommentDto> insert(String text, String bookId);

    Mono<UserCommentDto> update(String id, String text);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteAllByBookId(String bookId);
}
