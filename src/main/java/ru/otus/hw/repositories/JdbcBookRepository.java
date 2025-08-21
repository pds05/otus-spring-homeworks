package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final JdbcOperations jdbcOperations;

    private final NamedParameterJdbcTemplate namedParametersJdbcTemplate;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(namedParametersJdbcTemplate.query(
                "select b.id, b.title, b.author_id, a.full_name, bg.genre_id, g.name " +
                        "from books b " +
                        "join authors a on a.id = b.author_id  " +
                        "left join books_genres bg on bg.book_id = b.id  " +
                        "left join genres g on g.id = bg.genre_id  " +
                        "where b.id = :id",
                Collections.singletonMap("id", id), new BookResultSetExtractor()));
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
        namedParametersJdbcTemplate.update("delete from books where id = :id",
                Collections.singletonMap("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcOperations.query("select b.id, b.title, b.author_id, a.full_name " +
                "from books b " +
                "join authors a on a.id = b.author_id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcOperations.query("select book_id, genre_id from books_genres",
                (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"),
                        rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, g -> g));

        Map<Long, Book> bookMap = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, b -> b));

        Map<Long, List<Genre>> relationMap = relations.stream().collect(Collectors.groupingBy(
                relation -> relation.bookId, Collectors.mapping(relation -> {
                    if (genreMap.containsKey(relation.genreId)) {
                        return genreMap.get(relation.genreId);
                    } else {
                        throw new EntityNotFoundException("Genre with id " + relation.genreId + " not found");
                    }
                }, Collectors.toList())));

        relationMap.forEach((bookId, genreList) -> {
            if (bookMap.containsKey(bookId)) {
                Book book = bookMap.get(bookId);
                book.setGenres(genreList);
            } else {
                throw new EntityNotFoundException("Book with id " + bookId + " not found");
            }
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        int inserted = namedParametersJdbcTemplate.update("insert into books (title, author_id) " +
                "values (:title, :authorId)", namedParameters, keyHolder);
        if (inserted == 0) {
            throw new EntityNotFoundException("Book " + book + " not inserted");
        }

        book.setId(keyHolder.getKeyAs(Long.class));

        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId())
                .addValue("id", book.getId());

        int updated = namedParametersJdbcTemplate.update("update books set title = :title, author_id = :authorId " +
                "where id = :id", namedParameters);
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not updated");
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<SqlParameterSource> batchParam = new ArrayList<>(book.getGenres().size());
        book.getGenres().forEach(genre -> {
            batchParam.add(new MapSqlParameterSource()
                    .addValue("bookId", book.getId())
                    .addValue("genreId", genre.getId()));
        });

        namedParametersJdbcTemplate.batchUpdate("insert into books_genres values (:bookId, :genreId)",
                batchParam.toArray(SqlParameterSource[]::new));
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

            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));

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
