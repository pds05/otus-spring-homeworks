package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceReactiveImpl implements AuthorServiceReactive {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(AuthorDto::fromDomainObject);
    }
}
