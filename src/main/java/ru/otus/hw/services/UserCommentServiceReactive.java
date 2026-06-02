package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.UserCommentDto;

public interface UserCommentServiceReactive extends UserCommentService {

    Mono<UserCommentDto> findByIdReactive(String id);

    Flux<UserCommentDto> findAllByBookIdReactive(String bookId);

    Mono<UserCommentDto> insertReactive(String text, String bookId);

    Mono<UserCommentDto> updateReactive(String id, String text);

    Mono<Void> deleteByIdReactive(String id);

    Mono<Void> deleteAllByBookIdReactive(String bookId);
}
