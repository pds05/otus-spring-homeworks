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

import java.util.ArrayList;
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

    public static final int GENRES_PER_BOOK = 3;

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
        Flux<Genre> genreFlux = genreRepository.saveAll(generateGenres(GENRE_COUNT));
        log.debug("All genres saved");
        Flux<Author> authorFlux = authorRepository.saveAll(generateAuthors(AUTHOR_COUNT));
        log.debug("All authors saved");
        Flux<Book> bookFlux = bookRepository.saveAll(generateBooks(genreFlux.collectList().block(),
                authorFlux.collectList().block(), BOOK_COUNT));

        bookFlux.publishOn(Schedulers.boundedElastic())
                .doOnNext(book -> {
                    log.debug("Book saved: {}", book);
                    userCommentRepository.saveAll(generateUserComments(USER_COMMENT_PER_BOOKS, book.getId()))
                            .doOnComplete(() -> log.debug("All user comment saved for book: {}", book.getTitle()))
                            .subscribe(saved -> log.debug("Saved user comment: {}", saved));
                })
                .doOnComplete(() -> log.debug("All books saved"))
                .subscribe(saved -> log.debug("Saved book: {}", saved));
    }

    private List<Genre> generateGenres(int count) {
        List<Genre> genreList = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            genreList.add(new Genre(null, String.format("Genre_%d", i)));
        }
        return genreList;
    }

    private List<Author> generateAuthors(int count) {
        List<Author> authorList = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            authorList.add(new Author(null, String.format("Author_%d", i)));
        }
        return authorList;
    }

    private List<Book> generateBooks(List<Genre> genres, List<Author> authors, int count) {
        Random random = new Random();
        List<Book> bookList = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            List<Genre> genreList = new ArrayList<>();
            for (int j = 0; j < GENRES_PER_BOOK; j++) {
                Genre genre;
                do  {
                    genre = genres.get(random.nextInt(GENRE_COUNT));
                } while (genreList.contains(genre));
                genreList.add(genre);
            }
            Book book = new Book(IS_BOOK_AUTOGENERATE_ID ? null : String.valueOf(i), String.format("Book_Title_%d", i),
                    authors.get(random.nextInt(AUTHOR_COUNT)),
                    genreList
            );
            bookList.add(book);
        }
        return bookList;
    }

    private List<UserComment> generateUserComments(int count, String bookId) {
        List<UserComment> userCommentList = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            userCommentList.add(new UserComment(null, "Text_Comment_" + i, bookId));
        }
        return userCommentList;
    }
}
