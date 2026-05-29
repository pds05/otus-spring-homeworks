package ru.otus.hw.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.HashSet;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class ReactRestBooksController {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final UserCommentRepository userCommentRepository;

    @GetMapping(value = "/api/book")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAll().map(BookDto::fromDomainObject);
    }

    @GetMapping("/api/book/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookDto> getBook(@PathVariable String id) {
        return bookRepository.findById(id).map(BookDto::fromDomainObject);
    }

    @GetMapping("/api/book/{id}/user_comment")
    @ResponseStatus(HttpStatus.OK)
    public Flux<UserCommentDto> getUserComments(@PathVariable String id) {
        return userCommentRepository.findAllByBookId(id)
                .map(UserCommentDto::fromDomainObject);
    }

    @PostMapping(value = "/api/book",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> saveBook(@RequestBody BookFromUiDto bookDto) {
        return bookRepository.insert(BookFromUiDto.toDomainObject(bookDto))
                .map(BookDto::fromDomainObject);

    }

    @PutMapping(value = "/api/book/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<BookDto> updateBook(@RequestBody BookFromUiDto bookDto,
                                    @PathVariable("id") String bookId) {
        bookDto.setId(bookId);

        Mono<Book> bookMono = bookRepository.findById(bookId);
        Mono<Author> authorMono = authorRepository.findById(bookDto.getAuthorId());
        Mono<List<Genre>> genresMono = genreRepository.findAllByIdIn(new HashSet<>(bookDto.getGenreIds())).collectList();

        return Mono.zip(bookMono, authorMono, genresMono)
                .zipWhen(tuple3 -> {
                    Book book = tuple3.getT1();
                    Author author = tuple3.getT2();
                    List<Genre> genres = tuple3.getT3();

                    book.setTitle(bookDto.getTitle());
                    book.setAuthor(author);
                    book.setGenres(genres);

                    return bookRepository.save(book);
                })
                .map(tuple2 -> BookDto.fromDomainObject(tuple2.getT2()));
    }

    @DeleteMapping(value = "/api/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookRepository.deleteById(id);
    }
}
