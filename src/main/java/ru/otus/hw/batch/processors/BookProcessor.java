package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.GenreDoc;

@Component
@AllArgsConstructor
public class BookProcessor implements ItemProcessor<Book, BookDoc> {

    @Override
    public BookDoc process(Book item) throws Exception {
        BookDoc bookDoc = new BookDoc();
        bookDoc.setId(item.getId());
        bookDoc.setTitle(item.getTitle());
        bookDoc.setAuthorDoc(new AuthorDoc(item.getAuthor().getId(), item.getAuthor().getFullName()));
        bookDoc.setGenreDocs(item.getGenres().stream()
                .map(genre -> new GenreDoc(genre.getId(), genre.getName())).toList());
        return bookDoc;
    }
}
