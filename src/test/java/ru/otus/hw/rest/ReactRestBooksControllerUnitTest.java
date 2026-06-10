package ru.otus.hw.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.*;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.services.BookServiceReactive;
import ru.otus.hw.services.UserCommentServiceReactive;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = ReactRestBooksController.class)
public class ReactRestBooksControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private BookServiceReactive bookServiceReactive;
    @MockitoBean
    private UserCommentServiceReactive userCommentServiceReactive;

    private final List<BookDto> books = List.of(
            new BookDto("1",
                    "Book 1",
                    new AuthorDto("1", "Author 1"),
                    List.of(new GenreDto("1", "Genre 1"), new GenreDto("2", "Genre 2"))
            ),
            new BookDto("2",
                    "Book 2",
                    new AuthorDto("2", "Author 2"),
                    List.of(new GenreDto("3", "Genre 3"), new GenreDto("4", "Genre 4"))
            ),
            new BookDto("3",
                    "Book 3",
                    new AuthorDto("3", "Author 3"),
                    List.of(new GenreDto("1", "Genre 1"),
                            new GenreDto("2", "Genre 2"),
                            new GenreDto("3", "Genre 3"),
                            new GenreDto("4", "Genre 4"))
            ));

    @Test
    void shouldReturnBooks() throws Exception {
        when(bookServiceReactive.findAll()).thenReturn(Flux.fromIterable(books));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v2/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(books.size());

        verify(bookServiceReactive, times(1)).findAll();
    }

    @Test
    void shouldReturnBookById() throws Exception {
        Book book = createBook();
        BookDto bookDto = BookDto.fromDomainObject(book);

        when(bookServiceReactive.findById("1")).thenReturn(Mono.just(bookDto));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v2/book/{id}", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(book.getId())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenres().get(0).getId())
                .jsonPath("$.genres[1].id").isEqualTo(book.getGenres().get(1).getId())
                .jsonPath("$.author.id").isEqualTo(book.getAuthor().getId());

        verify(bookServiceReactive, times(1)).findById(book.getId());
    }

    @Test
    void shouldReturnUserCommentsForBook() throws Exception {
        List<UserComment> userComments = List.of(
                new UserComment("1", "Text 1", "1"),
                new UserComment("2", "Text 2", "1")
        );
        List<UserCommentDto> userCommentDtos = userComments.stream().map(UserCommentDto::fromDomainObject).toList();
        when(userCommentServiceReactive.findAllByBookId("1")).thenReturn(Flux.fromIterable(userCommentDtos));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v2/book/{id}/user_comment", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBodyList(UserCommentDto.class)
                .hasSize(2);

        verify(userCommentServiceReactive, times(1)).findAllByBookId("1");
    }

    @Test
    void shouldSaveBook() throws Exception {
        Book savedBook = createBook();
        BookDto savedBookDto = BookDto.fromDomainObject(savedBook);

        Book savingBook = new Book(null, savedBook.getTitle(), savedBook.getAuthor(), savedBook.getGenres());
        BookFromUiDto bookFromUiDto = BookFromUiDto.fromDomainObject(savingBook);

        when(bookServiceReactive.insert(anyString(), anyString(), anySet()))
                .thenReturn(Mono.just(savedBookDto));

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v2/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookFromUiDto), BookFromUiDto.class)
                .exchange();
        response.expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedBook.getId())
                .jsonPath("$.title").isEqualTo(savedBook.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(savedBook.getGenres().get(0).getId())
                .jsonPath("$.genres[1].id").isEqualTo(savedBook.getGenres().get(1).getId())
                .jsonPath("$.author.id").isEqualTo(savedBook.getAuthor().getId());

        verify(bookServiceReactive, times(1)).insert(anyString(), anyString(), anySet());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Book updatingBook = createBook();
        updatingBook.setTitle("New Title");
        updatingBook.setGenres(List.of(new Genre("3", "Genre_3"), new Genre("4", "Genre_4")));
        updatingBook.setAuthor(new Author("2", "Author_2"));

        BookDto bookDto = BookDto.fromDomainObject(updatingBook);
        BookFromUiDto bookFromUiDto = BookFromUiDto.fromDomainObject(updatingBook);

        when(bookServiceReactive.update(anyString(),anyString(), anyString(), anySet())).thenReturn(Mono.just(bookDto));

        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/api/v2/book/{id}", bookFromUiDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookFromUiDto), BookFromUiDto.class)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(bookDto.id())
                .jsonPath("$.title").isEqualTo(bookDto.title())
                .jsonPath("$.genres[0].id").isEqualTo(updatingBook.getGenres().get(0).getId())
                .jsonPath("$.genres[0].name").isEqualTo(updatingBook.getGenres().get(0).getName())
                .jsonPath("$.genres[1].id").isEqualTo(updatingBook.getGenres().get(1).getId())
                .jsonPath("$.genres[1].name").isEqualTo(updatingBook.getGenres().get(1).getName())
                .jsonPath("$.author.id").isEqualTo(updatingBook.getAuthor().getId())
                .jsonPath("$.author.fullName").isEqualTo(updatingBook.getAuthor().getFullName());

        verify(bookServiceReactive, times(1)).update(anyString(), anyString(), anyString(), anySet());
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        when(bookServiceReactive.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v2/book/{id}", "1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookServiceReactive, times(1)).deleteById("1");
    }

    private Book createBook() {
        return new Book("1",
                "Book Title",
                new Author("1", "Author 1"),
                List.of(new Genre("1", "Genre 1"), new Genre("2", "Genre 2")));
    }
}
