package ru.otus.hw.services;

import ru.otus.hw.dtos.UserCommentDto;

import java.util.List;

public interface UserCommentService {

    UserCommentDto findById(long id);

    List<UserCommentDto> findAllByBookId(long bookId);

    UserCommentDto insert(String text, long bookId);

    UserCommentDto update(long id, String text);

    void deleteById(long id);


}
