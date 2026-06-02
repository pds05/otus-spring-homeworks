package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.List;

@Service("userCommentService")
@RequiredArgsConstructor
public class UserCommentServiceReactiveImpl implements UserCommentServiceReactive {

    private final UserCommentRepository userCommentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public UserCommentDto findById(long id) {
        return userCommentRepository.getUserCommentById(String.valueOf(id))
                .map(UserCommentDto::fromDomainObject).blockOptional()
                .orElseThrow(() -> new EntityNotFoundException("User comment id %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserCommentDto> findAllByBookId(long bookId) {
        return userCommentRepository.findAllByBookId(String.valueOf(bookId))
                .map(UserCommentDto::fromDomainObject)
                .collectList().block();
    }

    @Transactional
    @Override
    public UserCommentDto insert(String text, long bookId) {
        Book book = bookRepository.findById(String.valueOf(bookId))
                .blockOptional()
                .orElseThrow(() -> new EntityNotFoundException("Book id %d not found".formatted(bookId)));
        UserComment userComment = new UserComment();
        userComment.setText(text);
        userComment.setBookId(book.getId());
        return userCommentRepository.save(userComment)
                .map(UserCommentDto::fromDomainObject).block();
    }

    @Transactional
    @Override
    public UserCommentDto update(long id, String text) {
        UserComment userComment = userCommentRepository.getUserCommentById(String.valueOf(id))
                .blockOptional()
                .orElseThrow(() -> new EntityNotFoundException("User comment id %d not found".formatted(id)));
        userComment.setText(text);
        userCommentRepository.save(userComment);

        return UserCommentDto.fromDomainObject(userComment);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        userCommentRepository.deleteById(String.valueOf(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<UserCommentDto> findByIdReactive(String id) {
        return userCommentRepository.findById(id).map(UserCommentDto::fromDomainObject);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<UserCommentDto> findAllByBookIdReactive(String bookId) {
        return userCommentRepository.findAllByBookId(bookId).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<UserCommentDto> insertReactive(String text, String bookId) {
        return bookRepository.findById(bookId).flatMap(book -> {
            UserComment userComment = new UserComment();
            userComment.setText(text);
            userComment.setBookId(book.getId());
            return userCommentRepository.save(userComment);
        }).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<UserCommentDto> updateReactive(String id, String text) {
        return userCommentRepository.findById(id)
                .flatMap(userComment -> {
                    userComment.setText(text);
                    return userCommentRepository.save(userComment);
                }).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByIdReactive(String id) {
        return userCommentRepository.deleteAllByBookId(id);
    }

    @Override
    public Mono<Void> deleteAllByBookIdReactive(String bookId) {
        return userCommentRepository.deleteAllByBookId(bookId);
    }
}
