package ru.otus.hw.batch.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.repositories.UserCommentRepository;

import java.util.Map;

@StepScope
@Component
public class UserCommentReader extends RepositoryItemReader<UserComment> {

    public UserCommentReader(UserCommentRepository userCommentRepository) {
        setRepository(userCommentRepository);
        setMethodName("findAll");
        setSort(Map.of("id", Sort.Direction.ASC));
    }
}
