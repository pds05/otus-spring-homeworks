package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;

@Service("userCommentService")
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {

    private final UserCommentRepository userCommentRepository;

    private final BookRepository bookRepository;

    private final UserCommentConverter userCommentConverter;

    @Transactional(readOnly = true)
    @Override
    public UserCommentDto findById(long id) {
        return userCommentRepository.getUserCommentById(id)
                .map(userCommentConverter::userCommentToDto)
                .orElseThrow(() -> new EntityNotFoundException("User comment id %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserCommentDto> findAllByBookId(long bookId) {
        return userCommentRepository.getAllUserCommentsByBookId(bookId).stream()
                .map(userCommentConverter::userCommentToDto)
                .toList();
    }

    @Transactional
    @Override
    public UserCommentDto insert(String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book id %d not found".formatted(bookId)));
        UserComment userComment = new UserComment();
        userComment.setText(text);
        userComment.setBook(book);
        UserComment savedUserComment = userCommentRepository.save(userComment);

        return userCommentConverter.userCommentToDto(savedUserComment);
    }

    @Transactional
    @Override
    public UserCommentDto update(long id, String text) {
        UserComment userComment = userCommentRepository.getUserCommentById(id)
                .orElseThrow(() -> new EntityNotFoundException("User comment id %d not found".formatted(id)));
        userComment.setText(text);
        userCommentRepository.save(userComment);

        return userCommentConverter.userCommentToDto(userComment);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        userCommentRepository.deleteById(id);
    }
}
