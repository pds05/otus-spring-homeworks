package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.UserComment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserCommentRepository implements UserCommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<UserComment> getUserCommentById(long id) {
        return Optional.ofNullable(em.find(UserComment.class, id));
    }

    @Override
    public List<UserComment> getAllUserCommentsByBookId(long bookId) {
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
