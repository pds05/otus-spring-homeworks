package ru.otus.hw.repositories;

import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentRepository {

    Optional<UserComment> getUserCommentById(long id);

    List<UserComment> getAllUserCommentsByBookId(long bookId);

    UserComment save(UserComment userComment);

    void deleteById(long id);
}
