package ru.otus.hw.models;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"author", "genres"})
@EqualsAndHashCode(exclude = {"author", "genres"})
@Document(collection = "books")
public class Book {
    @Id
    private String id;

    private String title;

    @DBRef(lazy = true)
    private Author author;

    @DBRef(lazy = true)
    private List<Genre> genres;

}
