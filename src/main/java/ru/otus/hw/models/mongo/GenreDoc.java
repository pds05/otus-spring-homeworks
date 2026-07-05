package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.models.Genre;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "genre_doc")
public class GenreDoc implements MongoDoc {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    public String buildId() {
        return UUID.nameUUIDFromBytes(this.getClass().getName().concat(getName()).getBytes()).toString();
    }

    public static GenreDoc fromGenre(Genre genre) {
        return new GenreDoc(null, genre.getName());
    }

}
