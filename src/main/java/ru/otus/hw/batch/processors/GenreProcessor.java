package ru.otus.hw.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.mongo.GenreDoc;

@Component
public class GenreProcessor implements ItemProcessor<Genre, GenreDoc> {
    @Override
    public GenreDoc process(Genre item) throws Exception {
        return new GenreDoc(item.getId(), item.toString());
    }
}
