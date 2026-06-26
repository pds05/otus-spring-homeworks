package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "book_docs")
public class BookDoc {

    @Id
    private long id;

    private String title;

    @DBRef(lazy = true)
    private AuthorDoc authorDoc;

    @DBRef(lazy = true)
    private List<GenreDoc> genreDocs;

}
