package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "genre_doc")
public class GenreDoc {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

}
