package ru.otus.hw.batch.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.MongoDoc;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoDocItemWriteListener implements ItemWriteListener<MongoDoc>  {

    private final Map<UUID, MongoDoc> mongoCache;

    @Override
    public void beforeWrite(Chunk<? extends MongoDoc> items) {
        items.getItems().forEach(item -> {
            if (item.getId() == null) {
                item.setId(item.buildId());
            }
        });
        log.debug("Writing items : {}", items);
    }

    @Override
    public void afterWrite(Chunk<? extends MongoDoc> items) {
        mongoCache.putAll(items.getItems().stream().collect(Collectors.toMap(i -> UUID.fromString(i.getId()), i -> i)));
        log.debug("Written items : {}", items);
    }

}
