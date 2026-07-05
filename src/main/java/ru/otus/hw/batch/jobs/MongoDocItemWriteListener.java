package ru.otus.hw.batch.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.MongoDoc;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoDocItemWriteListener implements ItemWriteListener<MongoDoc>  {

    @Override
    public void beforeWrite(Chunk<? extends MongoDoc> items) {
        log.debug("Writing items : {}", items);
    }

    @Override
    public void afterWrite(Chunk<? extends MongoDoc> items) {
        log.debug("Written items : {}", items);
    }

}
