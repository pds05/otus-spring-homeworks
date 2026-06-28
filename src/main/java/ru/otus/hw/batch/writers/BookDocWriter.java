package ru.otus.hw.batch.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.BookDoc;

@Component
public class BookDocWriter extends MongoItemWriter<BookDoc> {

    public BookDocWriter(MongoTemplate mongoTemplate) {
        setTemplate(mongoTemplate);
    }
}
