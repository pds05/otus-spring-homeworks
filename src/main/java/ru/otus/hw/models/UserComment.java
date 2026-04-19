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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
@Document(collection = "user_comments")
public class UserComment {
    @Id
    private String id;

    private String text;

    @DBRef(lazy = true)
    private Book book;

}
