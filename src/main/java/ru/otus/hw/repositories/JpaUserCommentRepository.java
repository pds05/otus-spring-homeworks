package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.UserComment;

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
        properties.put("jakarta.persistence.fetchgraph", entityGraph);

        return Optional.ofNullable(em.find(UserComment.class, id, properties));
    }

    @Override
    public List<UserComment> getAllUserCommentsByUserId(long userId) {
        EntityGraph<?> entityGraph = em.getEntityGraph("user-comment-entity-graph");
        return em.createQuery("select uc from UserComment uc where uc.user.id = :userId", UserComment.class)
                .setParameter("userId", userId)
                .setHint("javax.persistence.fetchgraph", entityGraph)
                .getResultList();
    }

    @Override
    public List<UserComment> getAllUserCommentsByBookId(long bookId) {
        EntityGraph<?> entityGraph = em.getEntityGraph("user-comment-entity-graph");
        return em.createQuery("select uc from UserComment uc where uc.book.id = :bookId", UserComment.class)
                .setParameter("bookId", bookId)
                .setHint("javax.persistence.fetchgraph", entityGraph)
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
        em.createQuery("delete from UserComment where id = :id")
                .setParameter("id", id)
                .executeUpdate();
        UserComment userComment = em.find(UserComment.class, id);
        em.detach(userComment);
    }
}
