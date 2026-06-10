package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.UserCommentRepository;

@Service("userCommentService")
@RequiredArgsConstructor
public class UserCommentServiceReactiveImpl implements UserCommentServiceReactive {

    private final UserCommentRepository userCommentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Mono<UserCommentDto> findById(String id) {
        return userCommentRepository.findById(id).map(UserCommentDto::fromDomainObject);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<UserCommentDto> findAllByBookId(String bookId) {
        return userCommentRepository.findAllByBookId(bookId).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<UserCommentDto> insert(String text, String bookId) {
        return bookRepository.findById(bookId).flatMap(book -> {
            UserComment userComment = new UserComment();
            userComment.setText(text);
            userComment.setBookId(book.getId());
            return userCommentRepository.save(userComment);
        }).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<UserCommentDto> update(String id, String text) {
        return userCommentRepository.findById(id)
                .flatMap(userComment -> {
                    userComment.setText(text);
                    return userCommentRepository.save(userComment);
                }).map(UserCommentDto::fromDomainObject);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return userCommentRepository.deleteAllByBookId(id);
    }

    @Override
    public Mono<Void> deleteAllByBookId(String bookId) {
        return userCommentRepository.deleteAllByBookId(bookId);
    }
}
