package ru.otus.hw.batch.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomItemWriteListener<T> implements ItemWriteListener<T> {

    @Override
    public void beforeWrite(Chunk<? extends T> items) {
        log.debug("Writing items : {}", items);
    }

    @Override
    public void afterWrite(Chunk<? extends T> items) {
        log.debug("Written items : {}", items);

    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends T> items) {
        log.error("Error writing items {}", items, exception);
    }
}
