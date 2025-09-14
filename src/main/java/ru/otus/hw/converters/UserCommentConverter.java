package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.UserComment;

@Component
public class UserCommentConverter {

    public String userCommentToString(UserComment userComment) {
        return "Id: %d, Book: %s, Text: %s".formatted(
                userComment.getId(),
                userComment.getBook().getTitle(),
                userComment.getText());
    }
}
