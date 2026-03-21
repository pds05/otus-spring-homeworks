package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCommentRepository extends JpaRepository<UserComment, Long> {
    Optional<UserComment> getUserCommentById(long id);

    List<UserComment> getUserCommentsByBookId(long bookId);

}
