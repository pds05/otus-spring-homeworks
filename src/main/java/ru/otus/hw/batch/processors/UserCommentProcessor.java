package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.MongoDoc;
import ru.otus.hw.models.mongo.UserCommentDoc;
import ru.otus.hw.repositories.mongo.BookDocRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class UserCommentProcessor implements ItemProcessor<UserComment, UserCommentDoc> {

    private BookDocRepository bookDocRepository;

    private Map<UUID, MongoDoc> mongoCache;

    @Override
    public UserCommentDoc process(UserComment item) throws Exception {
        UUID bookId = UUID.fromString(BookDoc.fromBook(item.getBook()).buildId());
        BookDoc bookDoc = (BookDoc) mongoCache.get(bookId);
        if (bookDoc == null) {
            log.debug("Book document is not in the cache, trying to request database, bookTitle={}", item.getBook().getTitle());
            bookDoc = bookDocRepository.findByTitle(item.getBook().getTitle())
                    .orElseThrow(() -> new RuntimeException("At first migrate books, use command 'us-b'"));
        }

        UserCommentDoc userCommentDoc = new UserCommentDoc();
        userCommentDoc.setText(item.getText());
        userCommentDoc.setBook(bookDoc);

        return userCommentDoc;
    }
}
