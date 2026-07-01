package ru.otus.hw.batch.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.UserCommentDoc;

@Slf4j
@Component
public class UserCommentDocItemWriteListener implements ItemWriteListener<UserCommentDoc>  {

    @Override
    public void beforeWrite(Chunk<? extends UserCommentDoc> items) {
        items.getItems().forEach(item -> {
            if (item.getId() == null) {
                item.setId(item.buildId());
            }
        });
        log.debug("Writing items : {}", items);
    }

    @Override
    public void afterWrite(Chunk<? extends UserCommentDoc> items) {
        log.debug("Written items : {}", items);
    }

}
