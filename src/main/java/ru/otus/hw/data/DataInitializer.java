package ru.otus.hw.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    public static final int GENRE_COUNT = 5;

    public static final int AUTHOR_COUNT = 5;

    public static final int BOOK_COUNT = 10;

    public static final int USER_COMMENT_PER_BOOKS = 5;

    public static final boolean IS_BOOK_AUTOGENERATE_ID = false;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private UserCommentRepository userCommentRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        genreRepository.saveAll(generateGenres(GENRE_COUNT))
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    log.info("All genres saved");
                    authorRepository.saveAll(generateAuthors(AUTHOR_COUNT))
                            .publishOn(Schedulers.boundedElastic())
                            .doOnComplete(() -> {
                                log.info("All authors saved");
                                bookRepository.saveAll(generateBooks(BOOK_COUNT))
                                        .publishOn(Schedulers.boundedElastic())
                                        .doOnNext(book -> {
                                            log.info("Book saved: {}", book);
                                            userCommentRepository.saveAll(generateUserComments(USER_COMMENT_PER_BOOKS, book.getId()))
                                                    .doOnComplete(() -> log.info("All user comment saved for book: {}", book.getTitle()))
                                                    .subscribe(saved -> log.info("Saved user comment: {}", saved));
                                        })
                                        .doOnComplete(() -> log.info("All books saved"))
                                        .subscribe(saved -> log.info("Saved book: {}", saved));
                            })
                            .subscribe(saved -> log.info("Saved author: {}", saved));

                })
                .subscribe(savedGenre -> log.info("Saved genre: {}", savedGenre));
    }

    private Flux<Genre> generateGenres(int count) {
        return Flux.generate(() -> 1, (state, sink) -> {
                    Genre genre = new Genre(null, String.format("Genre_%d", state++));
                    sink.next(genre);
                    if (state > count) {
                        sink.complete();
                    }
                    return state;
                })
                .cast(Genre.class)
                .doOnNext(genre -> log.info("Produced genre: {}", genre))
                .delayElements(Duration.ofMillis(100));
    }

    private Flux<Author> generateAuthors(int count) {
        return Flux.generate(() -> 1, (state, sink) -> {
                    Author author = new Author(null, String.format("Author_%d", state++));
                    sink.next(author);
                    if (state > count) {
                        sink.complete();
                    }
                    return state;
                })
                .cast(Author.class)
                .doOnNext(author -> log.info("Produced author: {}", author))
                .delayElements(Duration.ofMillis(100));
    }

    private Flux<Book> generateBooks(int count) {
        Random random = new Random();

        List<Author> authors = authorRepository.findAll().collectList().block();
        List<Genre> genres = genreRepository.findAll().collectList().block();

        return Flux.generate(() -> 1, (state, sink) -> {
                    Book book = new Book(IS_BOOK_AUTOGENERATE_ID ? null : state.toString(),
                            String.format("Book_Title_%d", state),
                            authors.get(random.nextInt(AUTHOR_COUNT)),
                            List.of(genres.get(random.nextInt(GENRE_COUNT)),
                                    genres.get(random.nextInt(GENRE_COUNT)))
                            );
                    sink.next(book);
                    if (state == count) {
                        sink.complete();
                    }
                    return ++state;
                })
                .cast(Book.class)
                .doOnNext(book -> log.info("Produced book: {}", book))
                .delayElements(Duration.ofMillis(100));
    }

    private Flux<UserComment> generateUserComments(int count, String bookId) {
        return Flux.generate(() -> 1, (state, sink) -> {
            UserComment userComment = new UserComment(null, "Text_Comment_" + state , bookId);
            sink.next(userComment);
            if (state == count) {
                sink.complete();
            }
            return ++state;
        })
                .cast(UserComment.class)
                .doOnNext(uc -> log.info("Produced user comment: {}", uc));
    }
}
