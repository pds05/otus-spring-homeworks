package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с комментариями к книгам")
@SpringBootTest
public class UserCommentServiceTest {

    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private UserCommentConverter userCommentConverter;

    @Test
    @DisplayName("должен добавлять новый комментарий к книге")
    void shouldInsertUserComment() {
        var returnedUserComment = userCommentService.insert("Test comment 1", 1);

        assertThat(returnedUserComment).isNotNull();
        assertThat(returnedUserComment.id()).isGreaterThan(0);

        System.out.println(userCommentConverter.userCommentDtoToString(returnedUserComment));

    }

    @Test
    @DisplayName("должен вернуть комментарий по его идентификатору")
    void shouldReturnUserCommentById() {
        var returnedUserComment = userCommentService.findById(2);

        assertThat(returnedUserComment).isNotNull();
        assertThat(returnedUserComment.id()).isEqualTo(2);

        System.out.println(userCommentConverter.userCommentDtoToString(returnedUserComment));

    }

    @Test
    @DisplayName("должен вернуть все комментарии к книге")
    void shouldReturnUserCommentsByBookId() {
        List<UserCommentDto> userCommentDtos = userCommentService.findAllByBookId(2);

        assertThat(userCommentDtos).isNotEmpty().hasSize(2);

        userCommentDtos.forEach(uc -> System.out.println(
                userCommentConverter.userCommentDtoToString(uc)));

    }

    @Test
    @DisplayName("должен изменить комментарий к книге")
    void shouldUpdateUserComment() {
        var expectedUserComment = userCommentService.findById(1);
        var updatedUserComment = userCommentService.update(expectedUserComment.id(), "Edited: " + expectedUserComment.text());

        assertThat(expectedUserComment.id()).isEqualTo(updatedUserComment.id());
        assertThat(expectedUserComment).isNotEqualTo(updatedUserComment);
        assertThat(updatedUserComment.text()).startsWith("Edited");

        System.out.println(userCommentConverter.userCommentDtoToString(updatedUserComment));

    }

    @Test
    @DisplayName("должен удалять комментарий по его идентификатору")
    void shouldDeleteUserComment() {
        userCommentService.deleteById(1);

        assertThrows(EntityNotFoundException.class, () -> userCommentService.findById(1));
    }
}
