package ru.otus.hw.batch.writers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.UserCommentDoc;
import ru.otus.hw.repositories.mongo.UserCommentDocRepository;

@StepScope
@Slf4j
@AllArgsConstructor
@Component
public class UserCommentDocWriter implements ItemWriter<UserCommentDoc> {

    private final UserCommentDocRepository userCommentDocRepository;

    @Override
    public void write(Chunk<? extends UserCommentDoc> chunk) throws Exception {
        log.debug("Writer started");
        for (UserCommentDoc userCommentDoc : chunk) {
            log.debug("Writer: saving data={}", userCommentDoc);
            userCommentDocRepository.save(userCommentDoc);
        }
        log.debug("Writer finished");
    }
}
