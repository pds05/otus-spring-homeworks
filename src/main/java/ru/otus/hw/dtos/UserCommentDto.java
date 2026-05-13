package ru.otus.hw.dtos;

import ru.otus.hw.models.UserComment;

public record UserCommentDto(Long id, String text, long bookId) {

    public static UserCommentDto fromDomainObject(UserComment userComment) {
        return new UserCommentDto(userComment.getId(), userComment.getText(), userComment.getId());
    }
}
