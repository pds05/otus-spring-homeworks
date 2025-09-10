package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.services.UserCommentService;
import ru.otus.hw.services.UserService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class UserCommands {

    private final UserService userService;

    private final UserCommentService userCommentService;

    private final UserConverter userConverterConverter;

    private final UserCommentConverter userCommentConverter;

    @ShellMethod(value = "Find all users", key = "au")
    public String findAllUsers() {
        return userService.findAll().stream()
                .map(userConverterConverter::userToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find user by id", key = "ubid")
    public String findUserById(long id) {
        return userService.findById(id)
                .map(userConverterConverter::userToString)
                .orElse("User with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find user by username", key = "ubun")
    public String findUserByUserName(String userName) {
        return userService.findByUsername(userName)
                .map(userConverterConverter::userToString)
                .orElse("User with username %s not found".formatted(userName));
    }

    @ShellMethod(value = "Save new user comment for the book", key = "ucins")
    public String insertUserComment(long userId, long bookId, String comment) {
        var savedUserComment = userCommentService.insert(comment, userId, bookId);
        return userCommentConverter.userCommentToString(savedUserComment);
    }

    @ShellMethod(value = "Update user comment by id", key = "ucupd")
    public String updateUserComment(long userCommentId, String comment) {
        var savedUserComment = userCommentService.update(userCommentId, comment);
        return userCommentConverter.userCommentToString(savedUserComment);
    }

    @ShellMethod(value = "Delete user comment by id", key = "ucdel")
    public void updateUserComment(long userCommentId) {
        userCommentService.deleteById(userCommentId);
    }

}
