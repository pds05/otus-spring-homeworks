package ru.otus.hw.mongock.changeunits;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

@AllArgsConstructor
@ChangeUnit(id = "init-authors", order = "002", author = "mongock")
public class MigrationAuthors {

    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void createCollection() {
        mongoTemplate.createCollection(Author.class);
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {

    }

    @Execution
    public void migrate() {
        mongoTemplate.save(new Author("Author_1"));
        mongoTemplate.save(new Author("Author_2"));
        mongoTemplate.save(new Author("Author_3"));
    }

    @RollbackExecution
    public void rollback() {

    }
}
