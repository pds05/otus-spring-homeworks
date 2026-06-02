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
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service("bookService")
public class BookServiceReactiveImpl implements BookServiceReactive {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final UserCommentRepository userCommentRepository;

    @Transactional(readOnly = true)
    @Override
    public BookDto findById (long id) {
        return bookRepository.findById(String.valueOf(id))
                .map(BookDto::fromDomainObject)
                .blockOptional()
                .orElseThrow(() -> new EntityNotFoundException("Book id %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll () {
        return bookRepository.findAll()
                .map(BookDto::fromDomainObject).collectList().block();
    }

    @Transactional
    @Override
    public BookDto insert(String title, long authorId, Set<Long> genresIds) {
        Mono<BookDto> bookDtoMono = save(null, title, String.valueOf(authorId),
                genresIds.stream().map(String::valueOf).collect(Collectors.toSet()));
        return bookDtoMono.block();
    }

    @Transactional
    @Override
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        Mono<BookDto> bookDtoMono = save(String.valueOf(id),
                title,
                String.valueOf(authorId),
                genresIds.stream().map(String::valueOf).collect(Collectors.toSet()));
        return bookDtoMono.block();
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(String.valueOf(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<BookDto> findByIdReactive(String id) {
        return bookRepository.findById(id).map(BookDto::fromDomainObject);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<BookDto> findAllReactive() {
        return bookRepository.findAll().map(BookDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<BookDto> insertReactive(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<BookDto> updateReactive(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByIdReactive(String id) {
        return bookRepository.deleteById(id)
                .then(Mono.defer(() -> userCommentRepository.deleteAllByBookId(id)));
    }

    public Mono<BookDto> save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var authorMono = authorRepository.findById(authorId);
        var genresMono = genreRepository.findAllByIdIn(genresIds).collectList();
        return Mono.zip(authorMono, genresMono).zipWhen(tuple2 -> {
            Author author = tuple2.getT1();
            List<Genre> genres = tuple2.getT2();
            if (isEmpty(genres) || genresIds.size() != genres.size()) {
                throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
            }
            var book = new Book(id, title, author, genres);
            return bookRepository.save(book);
        }).map(Tuple2::getT2).map(BookDto::fromDomainObject);
    }
}
