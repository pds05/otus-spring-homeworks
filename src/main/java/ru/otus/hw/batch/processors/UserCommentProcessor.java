package ru.otus.hw.batch.processors;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.UserCommentDoc;
import ru.otus.hw.repositories.mongo.BookDocRepository;

@Component
@AllArgsConstructor
public class UserCommentProcessor implements ItemProcessor<UserComment, UserCommentDoc> {

    private BookDocRepository bookDocRepository;

    @Override
    public UserCommentDoc process(UserComment item) throws Exception {
        BookDoc bookDoc = bookDocRepository.findByTitle(item.getBook().getTitle())
                .orElseThrow(() -> new RuntimeException("At first migrate books, use command 'us-b'"));

        UserCommentDoc userCommentDoc = new UserCommentDoc();
        userCommentDoc.setText(item.getText());
        userCommentDoc.setBook(bookDoc);

        return userCommentDoc;
    }
}
