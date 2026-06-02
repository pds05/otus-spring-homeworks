package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dtos.AuthorDto;

public interface AuthorServiceReactive extends AuthorService {
    Flux<AuthorDto> findAllReactive();

}
