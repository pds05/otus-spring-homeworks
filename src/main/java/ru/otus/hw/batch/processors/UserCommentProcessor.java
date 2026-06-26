package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.models.mongo.UserCommentDoc;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class UserCommentProcessor implements ItemProcessor<UserComment, UserCommentDoc> {

    private final Function<Book, BookDoc> bookMapper = book ->
            new BookDoc(book.getId(),
                    book.getTitle(),
                    new AuthorDoc(book.getAuthor().getId(), book.getAuthor().getFullName()),
                    book.getGenres().stream().map(genre -> new GenreDoc(genre.getId(), genre.getName()))
                            .toList());

    @Override
    public UserCommentDoc process(UserComment item) throws Exception {

        return new UserCommentDoc(item.getId(), item.getText(), bookMapper.apply(item.getBook()));
    }
}
