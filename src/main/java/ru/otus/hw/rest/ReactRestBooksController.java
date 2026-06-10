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
import ru.otus.hw.services.BookServiceReactive;
import ru.otus.hw.services.UserCommentServiceReactive;

import java.util.HashSet;

@RestController
@AllArgsConstructor
@Slf4j
public class ReactRestBooksController {

    private final BookServiceReactive bookServiceReactive;

    private final UserCommentServiceReactive userCommentServiceReactive;

    @GetMapping(value = "/api/v2/book")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BookDto> getAllBooks() {
        return bookServiceReactive.findAll();
    }

    @GetMapping("/api/v2/book/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookDto> getBook(@PathVariable String id) {
        return bookServiceReactive.findById(id);
    }

    @GetMapping("/api/v2/book/{id}/user_comment")
    @ResponseStatus(HttpStatus.OK)
    public Flux<UserCommentDto> getUserComments(@PathVariable String id) {
        return userCommentServiceReactive.findAllByBookId(id);
    }

    @PostMapping(value = "/api/v2/book",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> saveBook(@RequestBody BookFromUiDto bookDto) {
        return bookServiceReactive.insert(bookDto.getTitle(),
                bookDto.getAuthorId(),
                new HashSet<>(bookDto.getGenreIds()));
    }

    @PutMapping(value = "/api/v2/book/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<BookDto> updateBook(@RequestBody BookFromUiDto bookDto,
                                    @PathVariable("id") String bookId) {

        return bookServiceReactive.update(bookId,
                bookDto.getTitle(),
                bookDto.getAuthorId(),
                new HashSet<>(bookDto.getGenreIds()));
    }

    @DeleteMapping(value = "/api/v2/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookServiceReactive.deleteById(id);
    }
}
