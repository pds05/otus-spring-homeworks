package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.UserComment;

@Repository
public interface UserCommentRepository extends ReactiveMongoRepository<UserComment, String> {
    Mono<UserComment> getUserCommentById(String id);

    Flux<UserComment> findAllByBookId(String bookId);

    Mono<Void> deleteAllByBookId(String bookId);

}
