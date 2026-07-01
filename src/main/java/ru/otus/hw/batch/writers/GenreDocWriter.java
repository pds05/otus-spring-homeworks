package ru.otus.hw.batch.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.GenreDoc;

@Component
public class GenreDocWriter extends MongoItemWriter<GenreDoc> {

    public GenreDocWriter(MongoTemplate mongoTemplate) {
        setTemplate(mongoTemplate);
    }
}