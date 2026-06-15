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
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.UserCommentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
public class RestBooksController {

    private final BookService bookService;

    private final UserCommentService userCommentService;

    @GetMapping("/api/book")
    @ResponseStatus(HttpStatus.OK)
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/book/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto getBook(@PathVariable Long id) {
        BookDto book = bookService.findById(id);
        return new BookDto(book.id(),
                book.title(),
                book.author(),
                book.genres());
    }

    @GetMapping("/api/book/{id}/user_comment")
    @ResponseStatus(HttpStatus.OK)
    public List<UserCommentDto> getUserComments(@PathVariable Long id) {
        return userCommentService.findAllByBookId(id);
    }

    @PostMapping(value = "/api/book",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto saveBook(@RequestBody BookFromUiDto book) {
        return bookService.insert(book.getTitle(),
                book.getAuthorId(),
                book.getGenreIds().stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toSet()));
    }

    @PutMapping(value = "/api/book/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BookDto updateBook(@RequestBody BookFromUiDto book,
                              @PathVariable("id") Long bookId) {
        return bookService.update(bookId,
                book.getTitle(),
                book.getAuthorId(),
                book.getGenreIds().stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toSet()));
    }

    @DeleteMapping(value = "/api/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
