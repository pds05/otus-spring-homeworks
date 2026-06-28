package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.repositories.mongo.AuthorDocRepository;
import ru.otus.hw.repositories.mongo.GenreDocRepository;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class BookProcessor implements ItemProcessor<Book, BookDoc> {

    private AuthorDocRepository authorDocRepository;

    private GenreDocRepository genreDocRepository;

    @Override
    public BookDoc process(Book item) throws Exception {
        BookDoc bookDoc = new BookDoc();
        bookDoc.setId(null);
        bookDoc.setTitle(item.getTitle());

        AuthorDoc authorDoc = authorDocRepository.findByFullName(item.getAuthor().getFullName())
                .orElseThrow(() -> new RuntimeException("At first migrate authors, use command 'sm-a'"));
        bookDoc.setAuthorDoc(authorDoc);


        List<GenreDoc> genreDocs = genreDocRepository.findByNameIn(item.getGenres().stream()
                .map(Genre::getName).toList());
        if (genreDocs.isEmpty() || genreDocs.size()  != item.getGenres().size()) {
            throw new RuntimeException("At first migrate genres, use command 'sm-g'.");
        }
        bookDoc.setGenreDocs(genreDocs);
        return bookDoc;
    }
}
