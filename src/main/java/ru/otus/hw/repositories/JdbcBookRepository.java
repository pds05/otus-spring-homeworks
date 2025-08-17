package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private static AuthorRepository authorRepository;

    private final JdbcOperations jdbcOperations;

    private final GenreRepository genreRepository;

    @Autowired
    public void setAuthorRepository(AuthorRepository authorRepository) {
        JdbcBookRepository.authorRepository = authorRepository;
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(jdbcOperations.query("select * from books " +
                "inner join authors on authors.id = books.author_id  " +
                "left join books_genres on books_genres.book_id = books.id  " +
                "left join genres on genres.id = books_genres.genre_id  " +
                "where books.id = ?", new BookResultSetExtractor(), id));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbcOperations.update("delete from books where id = ?", id);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcOperations.query("select * from books", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcOperations.query("select * from books_genres",
                (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"),
                        rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            List<Genre> bookGenres = new ArrayList<>();
            for (BookGenreRelation relation : relations) {
                if (relation.bookId == book.getId()) {
                    bookGenres.add(genres.stream().filter(genre -> genre.getId() == relation.genreId)
                            .findFirst().orElseThrow(() ->
                                    new EntityNotFoundException("Genre with id " + relation.genreId + " not found")));
                }
            }
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(con -> {
            PreparedStatement ps = con.prepareStatement("insert into books (title, author_id) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setLong(2, book.getAuthor().getId());
            return ps;
        }, keyHolder);

        book.setId(keyHolder.getKeyAs(Long.class));

        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        int updated = jdbcOperations.update("update books set title = ?, author_id = ? where id =? ",
                book.getTitle(),
                book.getAuthor().getId(),
                book.getId());
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not updated");
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        jdbcOperations.batchUpdate("insert into books_genres values (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, book.getId());
                ps.setLong(2, book.getGenres().get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return book.getGenres().size();
            }
        });
    }

    private void removeGenresRelationsFor(Book book) {
        jdbcOperations.update("delete from books_genres where book_id = ?", book.getId());
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();

            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            Author author = authorRepository.findById(rs.getInt("author_id"))
                    .orElseThrow(() -> new EntityNotFoundException("Author not found"));
            book.setAuthor(author);

            return book;
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> bookById = new HashMap<>();
            while (rs.next()) {
                long bookId = rs.getLong("id");
                Book book = bookById.get(bookId);
                if (book == null) {
                    book = new Book();
                    book.setId(bookId);
                    book.setTitle(rs.getString("title"));

                    Author author = new Author();
                    author.setId(rs.getLong("author_id"));
                    author.setFullName(rs.getString("full_name"));

                    book.setAuthor(author);
                    book.setGenres(new ArrayList<>());
                    bookById.put(bookId, book);
                }
                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("name"));
                book.getGenres().add(genre);
            }
            return bookById.values().stream().findFirst().orElse(null);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
