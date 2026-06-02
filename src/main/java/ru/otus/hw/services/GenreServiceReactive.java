package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dtos.GenreDto;

public interface GenreServiceReactive extends GenreService {

    Flux<GenreDto> findAllReactive();

}
