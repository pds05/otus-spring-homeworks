package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.BookDoc;

import java.util.Optional;

public interface BookDocRepository extends MongoRepository<BookDoc, String> {

    Optional<BookDoc> findByTitle(String name);
}
