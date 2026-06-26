package ru.otus.hw.batch.writers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.repositories.mongo.GenreDocRepository;

@StepScope
@Slf4j
@AllArgsConstructor
@Component
public class GenreDocWriter implements ItemWriter<GenreDoc> {

    private final GenreDocRepository genreDocRepository;

    @Override
    public void write(Chunk<? extends GenreDoc> chunk) throws Exception {
        log.debug("Writer started");
        for (GenreDoc genreDoc : chunk) {
            log.debug("Writer: saving data={}", genreDoc);
            genreDocRepository.save(genreDoc);
        }
        log.debug("Writer finished");
    }
}