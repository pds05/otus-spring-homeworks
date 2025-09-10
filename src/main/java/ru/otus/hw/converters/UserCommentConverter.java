package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.UserComment;

@Component
public class UserCommentConverter {

    public String userCommentToString(UserComment userComment) {
        return "Id: %d, User: %s, Book: %s, Text: %s".formatted(
                userComment.getId(),
                userComment.getUser().getUserName(),
                userComment.getBook().getTitle(),
                userComment.getText());
    }
}
