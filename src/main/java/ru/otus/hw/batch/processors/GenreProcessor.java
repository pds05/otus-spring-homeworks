package ru.otus.hw.batch.processors;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.mongo.GenreDoc;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class GenreProcessor implements ItemProcessor<Genre, GenreDoc> {

    private final Map<String, GenreDoc> genreDocCache = new HashMap<>();

    @Override
    public GenreDoc process(Genre item) throws Exception {
        GenreDoc genreDoc = new GenreDoc(ObjectId.get().toString(), item.getName());
        genreDocCache.put(item.getName(), genreDoc);
        return genreDoc;
    }
}
