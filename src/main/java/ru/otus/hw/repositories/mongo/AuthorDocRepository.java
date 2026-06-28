package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.AuthorDoc;

import java.util.Optional;

public interface AuthorDocRepository extends MongoRepository<AuthorDoc, String> {

    Optional<AuthorDoc> findByFullName(String fullName);
}
