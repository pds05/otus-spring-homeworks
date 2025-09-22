package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserCommentRepository implements UserCommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<UserComment> getUserCommentById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("user-comment-entity-graph");

        Map<String, Object> properties = new HashMap<>();
        properties.put(EntityGraphType.FETCH.getKey(), entityGraph);

        return Optional.ofNullable(em.find(UserComment.class, id, properties));
    }

    @Override
    public List<UserComment> getAllUserCommentsByBookId(long bookId) {
        em.find(Book.class, bookId);
        return em.createQuery("select uc from UserComment uc where uc.book.id = :bookId", UserComment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public UserComment save(UserComment userComment) {
        if (userComment.getId() == 0) {
            em.persist(userComment);
            return userComment;
        } else {
            return em.merge(userComment);
        }
    }

    @Override
    public void deleteById(long id) {
        UserComment userComment = em.find(UserComment.class, id);
        em.remove(userComment);
    }
}
