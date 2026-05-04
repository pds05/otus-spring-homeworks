package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.services.UserCommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class UserCommentCommands {

    private final UserCommentService userCommentService;

    private final UserCommentConverter userCommentConverter;

    @ShellMethod(value = "Save new user comment for the book", key = "ucins")
    public String insertUserComment(String bookId, String comment) {
        var savedUserComment = userCommentService.insert(comment, bookId);
        return userCommentConverter.userCommentDtoToString(savedUserComment);
    }

    @ShellMethod(value = "Update user comment by id", key = "ucupd")
    public String updateUserComment(String userCommentId, String comment) {
        var savedUserComment = userCommentService.update(userCommentId, comment);
        return userCommentConverter.userCommentDtoToString(savedUserComment);
    }

    @ShellMethod(value = "Delete user comment by id", key = "ucdel")
    public void deleteUserComment(String userCommentId) {
        userCommentService.deleteById(userCommentId);
    }

    @ShellMethod(value = "Find all user comments by book id", key = "aucbbid")
    public String findAllUserCommentsByBookId(String bookId) {
        return userCommentService.findAllByBookId(bookId).stream()
                .map(userCommentConverter::userCommentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find user comment by id", key = "ucbid")
    public String findUserCommentById(String userCommentId) {
        return userCommentConverter.userCommentDtoToString(userCommentService.findById(userCommentId));
    }

}
