package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceReactiveImpl implements BookServiceReactive {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final UserCommentRepository userCommentRepository;

    @Transactional(readOnly = true)
    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id).map(BookDto::fromDomainObject);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll().map(BookDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<BookDto> insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<BookDto> update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.deleteById(id)
                .then(userCommentRepository.deleteAllByBookId(id));
    }

    public Mono<BookDto> save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var authorMono = authorRepository.findById(authorId);
        Mono<List<Genre>> genresMono = genreRepository.findAllByIdIn(genresIds).collectList().flatMap(genreList -> {
            if (isEmpty(genreList) || genresIds.size() != genreList.size()) {
                return Mono.error(new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds)));
            } else {
                return Mono.just(genreList);
            }
        });
        return Mono.zip(authorMono, genresMono).zipWhen(tuple2 -> {
            Author author = tuple2.getT1();
            List<Genre> genres = tuple2.getT2();
            var book = new Book(id, title, author, genres);
            return bookRepository.save(book);
        }).map(Tuple2::getT2).map(BookDto::fromDomainObject);
    }
}
