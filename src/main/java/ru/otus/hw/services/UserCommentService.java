package ru.otus.hw.services;

import ru.otus.hw.dtos.UserCommentDto;

import java.util.List;

public interface UserCommentService {
    UserCommentDto findById(String id);

    List<UserCommentDto> findAllByBookId(String bookId);

    UserCommentDto insert(String text, String bookId);

    UserCommentDto update(String id, String text);

    void deleteById(String id);


}
