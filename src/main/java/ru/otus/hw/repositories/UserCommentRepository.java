package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCommentRepository extends MongoRepository<UserComment, String> {
    Optional<UserComment> getUserCommentById(String id);

    List<UserComment> getUserCommentsByBookId(String bookId);

}
