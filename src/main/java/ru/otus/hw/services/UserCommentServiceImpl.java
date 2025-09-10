package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.User;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.UserCommentRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {

    private final UserCommentRepository userCommentRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<UserComment> findById(long id) {
        return userCommentRepository.getUserCommentById(id);
    }

    @Override
    public List<UserComment> findAllByUserId(long userId) {
        return userCommentRepository.getAllUserCommentsByUserId(userId);
    }

    @Override
    public List<UserComment> findAllByBookId(long bookId) {
        return userCommentRepository.getAllUserCommentsByBookId(bookId);
    }

    @Transactional
    @Override
    public UserComment insert(String text, long userId, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book id " + bookId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User id " + userId + " not found"));
        UserComment userComment = new UserComment();
        userComment.setText(text);
        userComment.setBook(book);
        userComment.setUser(user);
        return userCommentRepository.save(userComment);
    }

    @Transactional
    @Override
    public UserComment update(long id, String text) {
        UserComment userComment = userCommentRepository.getUserCommentById(id)
                .orElseThrow(() -> new EntityNotFoundException("User comment id " + id + " not found"));
        userComment.setText(text);
        return userCommentRepository.save(userComment);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        userCommentRepository.deleteById(id);
    }
}
