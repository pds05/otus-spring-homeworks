package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.BookDoc;

public interface BookDocRepository extends MongoRepository<BookDoc, Long> {
}
