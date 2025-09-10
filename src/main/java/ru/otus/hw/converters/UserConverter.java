package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.User;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final UserCommentConverter userCommentConverter;

    public String userToString(User user) {
        return "Id: %d, Username: %s, Comments: %s".formatted(
                user.getId(),
                user.getUserName(),
                user.getUserComments().stream().map(userCommentConverter::userCommentToString)
                        .map("{%s}"::formatted)
                        .collect(Collectors.joining(",")));
    }
}