package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.GenreDoc;

import java.util.Collection;
import java.util.List;

public interface GenreDocRepository extends MongoRepository<GenreDoc, String> {

    List<GenreDoc> findByNameIn(Collection<String> names);

}
