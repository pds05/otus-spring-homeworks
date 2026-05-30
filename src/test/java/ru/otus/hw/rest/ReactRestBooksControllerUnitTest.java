package ru.otus.hw.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = ReactRestBooksController.class)
public class ReactRestBooksControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private BookRepository bookRepository;
    @MockitoBean
    private AuthorRepository authorRepository;
    @MockitoBean
    private GenreRepository genreRepository;
    @MockitoBean
    private UserCommentRepository userCommentRepository;

    private final List<Book> books = List.of(
            new Book("1",
                    "Book 1",
                    new Author("1", "Author 1"),
                    List.of(new Genre("1", "Genre 1"), new Genre("2", "Genre 2"))
            ),
            new Book("2",
                    "Book 2",
                    new Author("2", "Author 2"),
                    List.of(new Genre("3", "Genre 3"), new Genre("4", "Genre 4"))
            ),
            new Book("3",
                    "Book 3",
                    new Author("3", "Author 3"),
                    List.of(new Genre("1", "Genre 1"),
                            new Genre("2", "Genre 2"),
                            new Genre("3", "Genre 3"),
                            new Genre("4", "Genre 4"))
            ));

    @Test
    void shouldReturnBooks() throws Exception {
        when(bookRepository.findAll()).thenReturn(Flux.fromIterable(books));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(books.size());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBookById() throws Exception {
        Book book = createBook();

        when(bookRepository.findById("1")).thenReturn(Mono.just(book));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book/{id}", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(book.getId())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenres().get(0).getId())
                .jsonPath("$.genres[1].id").isEqualTo(book.getGenres().get(1).getId())
                .jsonPath("$.author.id").isEqualTo(book.getAuthor().getId());

        verify(bookRepository, times(1)).findById(book.getId());
    }

    @Test
    void shouldReturnUserCommentsForBook() throws Exception {
        List<UserComment> userComments = List.of(
                new UserComment("1", "Text 1", "1"),
                new UserComment("2", "Text 2", "1")
        );
        when(userCommentRepository.findAllByBookId("1")).thenReturn(Flux.fromIterable(userComments));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book/{id}/user_comment", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBodyList(UserCommentDto.class)
                .hasSize(2);

        verify(userCommentRepository, times(1)).findAllByBookId("1");
    }

    @Test
    void shouldSaveBook() throws Exception {
        Book savedBook = createBook();

        Book savingBook = new Book(null, savedBook.getTitle(), savedBook.getAuthor(), savedBook.getGenres());
        BookFromUiDto bookFromUiDto = BookFromUiDto.fromDomainObject(savingBook);

        when(bookRepository.insert(any(Book.class)))
                .thenReturn(Mono.just(savedBook));
        when(authorRepository.findById(savingBook.getAuthor().getId()))
                .thenReturn(Mono.just(savedBook.getAuthor()));
        when(genreRepository.findAllByIdIn(savingBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet())))
                .thenReturn(Flux.fromIterable(savedBook.getGenres()));

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/book")
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

        verify(bookRepository, times(1)).insert(any(Book.class));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Book book = createBook();

        BookFromUiDto bookDto = BookFromUiDto.fromDomainObject(book);
        bookDto.setTitle("New Title");
        bookDto.setGenreIds(List.of("3", "4"));
        bookDto.setAuthorId("2");

        when(bookRepository.findById(book.getId())).thenReturn(Mono.just(book));
        when(authorRepository.findById(bookDto.getAuthorId()))
                .thenReturn(Mono.just(new Author("2", "Author 2")));
        when(genreRepository.findAllByIdIn(new HashSet<>(bookDto.getGenreIds())))
                .thenReturn(Flux.fromIterable(List.of(new Genre("3", "Genre 3"),
                        new Genre("4", "Genre 4"))));
        when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));

        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/api/book/{id}", bookDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookDto), BookFromUiDto.class)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(bookDto.getId())
                .jsonPath("$.title").isEqualTo(bookDto.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(bookDto.getGenreIds().get(0))
                .jsonPath("$.genres[0].name").isEqualTo(book.getGenres().get(0).getName())
                .jsonPath("$.genres[1].id").isEqualTo(bookDto.getGenreIds().get(1))
                .jsonPath("$.genres[1].name").isEqualTo(book.getGenres().get(1).getName())
                .jsonPath("$.author.id").isEqualTo(bookDto.getAuthorId())
                .jsonPath("$.author.fullName").isEqualTo("Author 2");


        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        when(bookRepository.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/book/{id}", "1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookRepository, times(1)).deleteById("1");
        verify(userCommentRepository, times(1)).deleteAllByBookId("1");
    }

    private Book createBook() {
        return new Book("1",
                "Book Title",
                new Author("1", "Author 1"),
                List.of(new Genre("1", "Genre 1"), new Genre("2", "Genre 2")));
    }
}
