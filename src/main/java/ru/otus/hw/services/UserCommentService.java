package ru.otus.hw.services;

import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentService {
    Optional<UserComment> findById(long id);

    List<UserComment> findAllByUserId(long userId);

    List<UserComment> findAllByBookId(long bookId);

    UserComment insert(String text, long userId, long bookId);

    UserComment update(long id, String text);

    void deleteById(long id);


}
