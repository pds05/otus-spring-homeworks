package ru.otus.hw.dtos;

import ru.otus.hw.models.UserComment;

public record UserCommentDto(String id, String text, String bookId) {

    public static UserCommentDto fromDomainObject(UserComment userComment) {
        return new UserCommentDto(userComment.getId(), userComment.getText(), userComment.getBookId());
    }
}
