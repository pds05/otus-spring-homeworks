package ru.otus.hw.models.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.hw.models.UserComment;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "user_comment_docs")
public class UserCommentDoc implements MongoDoc {

    @Id
    private String id;

    private String text;

    @DBRef(lazy = true)
    private BookDoc book;

    @Override
    public String buildId() {
        return UUID.nameUUIDFromBytes(this.getClass().getName().concat(getText()).getBytes()).toString();
    }

    public static UserCommentDoc fromUserComment(UserComment userComment) {
        return new UserCommentDoc(null, userComment.getText(), BookDoc.fromBook(userComment.getBook()));
    }
}
