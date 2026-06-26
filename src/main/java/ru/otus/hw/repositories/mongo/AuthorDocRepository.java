package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.AuthorDoc;

public interface AuthorDocRepository extends MongoRepository<AuthorDoc, Long> {
}
