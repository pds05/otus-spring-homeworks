package ru.otus.hw.batch.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.UserCommentDoc;

@Component
public class UserCommentDocWriter extends MongoItemWriter<UserCommentDoc> {

    public UserCommentDocWriter(MongoTemplate mongoTemplate) {
        setTemplate(mongoTemplate);
    }
}
