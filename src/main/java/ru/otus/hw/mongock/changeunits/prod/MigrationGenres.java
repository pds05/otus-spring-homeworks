package ru.otus.hw.mongock.changeunits.prod;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

@AllArgsConstructor
@ChangeUnit(id="init-genres", order="001", author="mongock")
public class MigrationGenres {

    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void createCollection() {
        mongoTemplate.createCollection(Genre.class);
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
    }

    @Execution
    public void migrate() {
        mongoTemplate.save(new Genre("Genre_1"));
        mongoTemplate.save(new Genre("Genre_2"));
        mongoTemplate.save(new Genre("Genre_3"));
    }

    @RollbackExecution
    public void rollback() {
    }
}
