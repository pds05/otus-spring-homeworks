package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("authorService")
public class AuthorServiceReactiveImpl implements AuthorServiceReactive {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(AuthorDto::fromDomainObject)
                .collectList()
                .block();
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<AuthorDto> findAllReactive() {
        return authorRepository.findAll().map(AuthorDto::fromDomainObject);
    }
}
