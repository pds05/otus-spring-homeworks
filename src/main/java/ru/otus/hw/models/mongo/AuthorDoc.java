package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.models.Author;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "author_docs")
public class AuthorDoc implements MongoDoc {

    @Id
    private String id;

    @Indexed(unique = true)
    private String fullName;

    @Override
    public String buildId() {
        return UUID.nameUUIDFromBytes(this.getClass().getName().concat(getFullName()).getBytes()).toString();
    }

    public static AuthorDoc fromAuthor(Author author) {
        return new AuthorDoc(null, author.getFullName());
    }
}
