package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<User> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("user-entity-graph");

        return em.createQuery("select u from User u", User.class)
                .setHint("jakarta.persistence.fetchgraph", entityGraph)
                .getResultList();
    }

    @Override
    public Optional<User> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("user-entity-graph");

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.fetchgraph", entityGraph);

        return Optional.ofNullable(em.find(User.class, id, properties));
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        return Optional.ofNullable(em.createQuery(
                        "select u from User u left join fetch u.userComments where u.userName = :username", User.class)
                .setParameter("username", userName)
                .getSingleResult());
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    @Override
    public void deleteById(long id) {
        User user = em.find(User.class, id);
        em.remove(user);
    }
}
