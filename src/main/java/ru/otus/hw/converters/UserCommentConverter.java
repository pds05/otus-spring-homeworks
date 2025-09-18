package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.models.UserComment;

@Component
@RequiredArgsConstructor
public class UserCommentConverter {

    private final BookConverter bookConverter;

    public String userCommentToString(UserComment userComment) {
        return "Id: %d, Book: %s, Text: %s".formatted(
                userComment.getId(),
                bookConverter.bookToString(userComment.getBook()),
                userComment.getText());
    }

    public String userCommentDtoToString(UserCommentDto userCommentDto) {
        return "Id: %d, BookId: %d, Text: %s".formatted(
                userCommentDto.id(),
                userCommentDto.bookId(),
                userCommentDto.text());
    }

    public UserCommentDto userCommentToDto(UserComment userComment) {
        return new UserCommentDto(userComment.getId(),
                userComment.getText(),
                userComment.getBook().getId()
        );
    }
}