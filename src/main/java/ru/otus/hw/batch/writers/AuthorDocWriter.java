package ru.otus.hw.batch.writers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.repositories.mongo.AuthorDocRepository;

@StepScope
@Slf4j
@AllArgsConstructor
@Component
public class AuthorDocWriter implements ItemWriter<AuthorDoc> {

    private final AuthorDocRepository authorDocRepository;

    @Override
    public void write(Chunk<? extends AuthorDoc> chunk) throws Exception {
        log.debug("Writer started");
        for (AuthorDoc authorDoc : chunk) {
            log.debug("Writer: saving data={}", authorDoc);
            authorDocRepository.save(authorDoc);
        }
        log.debug("Writer finished");
    }
}
