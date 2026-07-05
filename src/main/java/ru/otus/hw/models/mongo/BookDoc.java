package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "book_docs")
public class BookDoc implements MongoDoc {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    @DBRef(lazy = true)
    private AuthorDoc authorDoc;

    @DBRef(lazy = true)
    private List<GenreDoc> genreDocs;

    @Override
    public String buildId() {
        return UUID.nameUUIDFromBytes(this.getClass().getName().concat(getTitle()).getBytes()).toString();
    }

    public static BookDoc fromBook(Book book) {
        return new BookDoc(ObjectId.get().toString(),
                book.getTitle(),
                AuthorDoc.fromAuthor(book.getAuthor()),
                book.getGenres().stream().map(GenreDoc::fromGenre).toList());
    }
}
