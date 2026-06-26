package ru.otus.hw.batch.writers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.BookDoc;

import ru.otus.hw.repositories.mongo.BookDocRepository;

@StepScope
@Slf4j
@AllArgsConstructor
@Component
public class BookDocWriter implements ItemWriter<BookDoc> {

    private final BookDocRepository bookDocRepository;

    @Override
    public void write(Chunk<? extends BookDoc> chunk) throws Exception {
        log.debug("Writer started");
        for (BookDoc bookDoc : chunk) {
            log.debug("Writer: saving data={}", bookDoc);
            bookDocRepository.save(bookDoc);
        }
        log.debug("Writer finished");
    }
}
