package ru.otus.hw.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.otus.hw.DataInitializer;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.BookFromUiDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReactRestBooksControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoSpyBean
    BookRepository bookRepository;

    @MockitoSpyBean
    UserCommentRepository userCommentRepository;

    @MockitoSpyBean
    AuthorRepository authorRepository;

    @MockitoSpyBean
    GenreRepository genreRepository;

    @BeforeEach
    void waitFillDb() throws InterruptedException {
        bookRepository.findById(String.valueOf(DataInitializer.BOOK_COUNT))
                .repeat()
                .blockFirst();
    }

    @Test
    void shouldReturnBooks() throws Exception {
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBodyList(BookDto.class);

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBookById() throws Exception {
        List<Genre> genres = genreRepository.findAll()
                .take(2)
                .collectList().block();
        Author author = authorRepository.findAll()
                .elementAt(0).block();

        Book book = new Book();
        book.setTitle("Book Title");
        book.setAuthor(author);
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book).block();
        assert savedBook != null;

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book/{id}", savedBook.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedBook.getId())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenres().get(0).getId())
                .jsonPath("$.genres[1].id").isEqualTo(book.getGenres().get(1).getId())
                .jsonPath("$.author.id").isEqualTo(book.getAuthor().getId());

        verify(bookRepository, times(1)).findById(savedBook.getId());
    }

    @Test
    void shouldReturnUserCommentsForBook() throws Exception {
        Book book = bookRepository.findAll().elementAt(0).block();
        assert book != null;

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/book/{id}/user_comment", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].bookId").isEqualTo(book.getId());

        verify(userCommentRepository, times(1)).findAllByBookId(book.getId());
    }

    @Test
    void shouldSaveBook() throws Exception {
        List<Genre> genres = genreRepository.findAll()
                .take(2)
                .collectList().block();
        assert genres != null;

        Author author = authorRepository.findAll()
                .elementAt(0).block();
        assert author != null;

        BookFromUiDto book = new BookFromUiDto(null, "Book Title",
                author.getId(),
                List.of(genres.get(0).getId(), genres.get(1).getId()));

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(book), BookFromUiDto.class)
                .exchange();
        response.expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(book.getGenreIds().get(0))
                .jsonPath("$.genres[1].id").isEqualTo(book.getGenreIds().get(1))
                .jsonPath("$.author.id").isEqualTo(book.getAuthorId());

        verify(bookRepository, times(1)).insert(any(Book.class));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        List<Genre> genres = genreRepository.findAll()
                .take(4)
                .collectList().block();
        assert genres != null;

        List<Author> authors = authorRepository.findAll()
                .take(2).collectList().block();
        assert authors != null;

        Book book = new Book();
        book.setTitle("Book Title");
        book.setGenres(List.of(genres.get(0), genres.get(1)));
        book.setAuthor(authors.get(0));

        Book savedBook = bookRepository.insert(book).block();
        assert savedBook != null;

        BookFromUiDto bookDto = BookFromUiDto.fromDomainObject(savedBook);
        bookDto.setTitle("New Title");
        bookDto.setGenreIds(List.of(genres.get(2).getId(), genres.get(3).getId()));
        bookDto.setAuthorId(authors.get(1).getId());

        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/api/book/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(bookDto), BookFromUiDto.class)
                .exchange();
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(bookDto.getId())
                .jsonPath("$.title").isEqualTo(bookDto.getTitle())
                .jsonPath("$.genres[0].id").isEqualTo(bookDto.getGenreIds().get(0))
                .jsonPath("$.genres[1].id").isEqualTo(bookDto.getGenreIds().get(1))
                .jsonPath("$.author.id").isEqualTo(bookDto.getAuthorId());

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        List<Genre> genres = genreRepository.findAll().take(2)
                .collectList().block();
        assert genres != null;

        Author author = authorRepository.findAll()
                .elementAt(0).block();
        assert author != null;

        Book book = new Book();
        book.setTitle("Book Title");
        book.setGenres(List.of(genres.get(0), genres.get(1)));
        book.setAuthor(author);

        Book savedBook = bookRepository.insert(book).block();
        assert savedBook != null;

        webTestClient.delete().uri("/api/book/{id}", savedBook.getId())
                .exchange()
                .expectStatus().isNoContent();

        verify(bookRepository, times(1)).deleteById(savedBook.getId());

        bookRepository.findById(savedBook.getId()).hasElement()
                .subscribe(Assertions::assertFalse);
    }
}