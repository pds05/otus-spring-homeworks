package ru.otus.hw.batch.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorDoc;

@Component
public class AuthorDocWriter extends MongoItemWriter<AuthorDoc> {

    public AuthorDocWriter(MongoTemplate mongoTemplate) {
        setTemplate(mongoTemplate);

    }

}
