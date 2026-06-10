package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceReactiveImpl implements GenreServiceReactive {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(GenreDto::fromDomainObject);
    }
}
