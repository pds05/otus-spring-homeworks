package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCommentRepository extends JpaRepository<UserComment, Long> {
    @EntityGraph("user-comment-entity-graph")
    Optional<UserComment> getUserCommentById(long id);

    @EntityGraph("user-comment-entity-graph")
    List<UserComment> getAllUserCommentsByUserId(long userId);

    @EntityGraph("user-comment-entity-graph")
    List<UserComment> getAllUserCommentsByBookId(long bookId);

}
