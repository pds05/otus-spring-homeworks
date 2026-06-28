package ru.otus.hw.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.mongo.AuthorDoc;

@Component
public class AuthorProcessor implements ItemProcessor<Author, AuthorDoc> {

    @Override
    public AuthorDoc process(Author item) throws Exception {
        return new AuthorDoc(null, item.getFullName());
    }

}
