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
    public String insertUserComment(long bookId, String comment) {
        var savedUserComment = userCommentService.insert(comment, bookId);
        return userCommentConverter.userCommentDtoToString(savedUserComment);
    }

    @ShellMethod(value = "Update user comment by id", key = "ucupd")
    public String updateUserComment(long userCommentId, String comment) {
        var savedUserComment = userCommentService.update(userCommentId, comment);
        return userCommentConverter.userCommentDtoToString(savedUserComment);
    }

    @ShellMethod(value = "Delete user comment by id", key = "ucdel")
    public void deleteUserComment(long userCommentId) {
        userCommentService.deleteById(userCommentId);
    }

    @ShellMethod(value = "Find all user comments by book id", key = "aucbbid")
    public String findAllUserCommentsByBookId(long bookId) {
        return userCommentService.findAllByBookId(bookId).stream()
                .map(userCommentConverter::userCommentDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find user comment by id", key = "ucbid")
    public String findUserCommentById(long userCommentId) {
        return userCommentConverter.userCommentDtoToString(userCommentService.findById(userCommentId));
    }

}
