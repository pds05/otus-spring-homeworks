package ru.otus.hw.batch.processors;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.mongo.AuthorDoc;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class AuthorProcessor implements ItemProcessor<Author, AuthorDoc> {

    private final Map<String, AuthorDoc> authorDocCache = new HashMap<>();

    @Override
    public AuthorDoc process(Author item) throws Exception {
        AuthorDoc authorDoc = new AuthorDoc(ObjectId.get().toString(), item.getFullName());
        authorDocCache.put(authorDoc.getId(), authorDoc);
        return authorDoc;
    }

}
