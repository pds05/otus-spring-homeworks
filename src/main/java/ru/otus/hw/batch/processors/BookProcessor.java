package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.models.mongo.MongoDoc;
import ru.otus.hw.repositories.mongo.AuthorDocRepository;
import ru.otus.hw.repositories.mongo.GenreDocRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class BookProcessor implements ItemProcessor<Book, BookDoc> {

    private AuthorDocRepository authorDocRepository;

    private GenreDocRepository genreDocRepository;

    private Map<UUID, MongoDoc> mongoCache;

    @Override
    public BookDoc process(Book item) throws Exception {
        BookDoc bookDoc = new BookDoc();
        bookDoc.setId(null);
        bookDoc.setTitle(item.getTitle());

        UUID authorId = UUID.fromString(AuthorDoc.fromAuthor(item.getAuthor()).buildId());
        AuthorDoc authorDoc = (AuthorDoc) mongoCache.get(authorId);
        if (authorDoc == null) {
            log.debug("Author document is not in the cache, trying to request database, " +
                    "authorFullName={}", item.getAuthor().getFullName());
            authorDoc = authorDocRepository.findByFullName(item.getAuthor().getFullName())
                    .orElseThrow(() -> new RuntimeException("At first migrate authors, use command 'sm-a'"));
        }
        bookDoc.setAuthorDoc(authorDoc);

        List<GenreDoc> cachedGenreList = new ArrayList<>();

        item.getGenres().forEach(genre -> {
            UUID genreId = UUID.fromString(GenreDoc.fromGenre(genre).buildId());
            if (mongoCache.containsKey(genreId)) {
                cachedGenreList.add((GenreDoc) mongoCache.get(genreId));
            }
        });

        if (cachedGenreList.size() != item.getGenres().size()) {
            List<String> requestNameList = item.getGenres().stream().map(GenreDoc::fromGenre).filter(genre -> !cachedGenreList.contains(genre)).map(g -> g.getName()).toList();
            log.debug("Genre documents is not in the cache, trying to request database, genreNames={}", requestNameList);
            List<GenreDoc> replyGenreList = genreDocRepository.findByNameIn(requestNameList);
            cachedGenreList.addAll(replyGenreList);
        }

        if (cachedGenreList.size() != item.getGenres().size()) {
            throw new RuntimeException("At first migrate genres, use command 'sm-g'.");
        }

        bookDoc.setGenreDocs(cachedGenreList);
        return bookDoc;
    }
}
