package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;
import java.util.Optional;

@Service("userCommentService")
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {

    private final UserCommentRepository userCommentRepository;

    private final BookRepository bookRepository;

    private final UserCommentConverter userCommentConverter;

    @Override
    public UserCommentDto findById(String id) {
        Optional<UserComment> userComment = userCommentRepository.findById(id);
        return userCommentConverter.userCommentToDto(userComment
                .orElseThrow(() -> new EntityNotFoundException("User comment id %s not found".formatted(id))));
    }

    @Override
    public List<UserCommentDto> findAllByBookId(String bookId) {
        return userCommentRepository.getUserCommentsByBookId(bookId).stream()
                .map(userCommentConverter::userCommentToDto)
                .toList();
    }

    @Override
    public UserCommentDto insert(String text, String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book id %s not found".formatted(bookId)));
        UserComment userComment = new UserComment();
        userComment.setText(text);
        userComment.setBook(book);
        UserComment savedUserComment = userCommentRepository.save(userComment);

        return userCommentConverter.userCommentToDto(savedUserComment);
    }

    @Override
    public UserCommentDto update(String id, String text) {
        UserComment userComment = userCommentRepository.getUserCommentById(id)
                .orElseThrow(() -> new EntityNotFoundException("User comment id %s not found".formatted(id)));
        userComment.setText(text);
        userCommentRepository.save(userComment);

        return userCommentConverter.userCommentToDto(userComment);
    }

    @Override
    public void deleteById(String id) {
        userCommentRepository.deleteById(id);
    }
}
