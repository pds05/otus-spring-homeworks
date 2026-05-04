package ru.otus.hw.mongock.changeunits.test;

import io.mongock.api.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@AllArgsConstructor
@ChangeUnit(id="init-user-comment", order="004", author="mongock")
public class MigrationUserComments {

    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void createCollection() {
        mongoTemplate.createCollection("user_comments");
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
    }

    @Execution
    public void migrate() {
        Book book = mongoTemplate.findOne(query(where("title").is("BookTitle_1")), Book.class);
        UserComment userComment = new UserComment();
        userComment.setText("Test comment 1-1");
        userComment.setBook(book);
        mongoTemplate.save(userComment);

        userComment = new UserComment();
        userComment.setText("Test comment 1-2");
        userComment.setBook(book);
        mongoTemplate.save(userComment);

        book = mongoTemplate.findOne(query(where("title").is("BookTitle_2")), Book.class);
        userComment = new UserComment();
        userComment.setText("Test comment 2-1");
        userComment.setBook(book);
        mongoTemplate.save(userComment);

        userComment = new UserComment();
        userComment.setText("Test comment 2-2");
        userComment.setBook(book);
        mongoTemplate.save(userComment);

        book = mongoTemplate.findOne(query(where("title").is("BookTitle_3")), Book.class);
        userComment = new UserComment();
        userComment.setText("Test comment 3-1");
        userComment.setBook(book);
        mongoTemplate.save(userComment);

        userComment = new UserComment();
        userComment.setText("Test comment 3-2");
        userComment.setBook(book);
        mongoTemplate.save(userComment);
    }

    @RollbackExecution
    public void rollback() {
    }

}
