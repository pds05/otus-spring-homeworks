package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service("bookService")
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    private final UserCommentRepository userCommentRepository;

    @Override
    public BookDto findById(String id) {
        Optional<Book> book = bookRepository.findById(id);
        return bookConverter.bookToDto(book.
                orElseThrow(() -> new EntityNotFoundException("Book id %s not found".formatted(id))));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookConverter::bookToDto)
                .toList();
    }

    @Override
    public BookDto insert(String title, String authorId, Set<String> genresIds) {
        Book book = save(null, title, authorId, genresIds);
        return bookConverter.bookToDto(book);
    }

    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genresIds) {
        Book book = save(id, title, authorId, genresIds);
        return bookConverter.bookToDto(book);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
        userCommentRepository.deleteAllByBookId(id);
    }

    private Book save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        return bookRepository.save(book);
    }
}
