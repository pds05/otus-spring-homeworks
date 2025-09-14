package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("user-entity-graph")
    List<User> findAll();

    @EntityGraph("user-entity-graph")
    Optional<User> findById(long id);

    @EntityGraph("user-entity-graph")
    Optional<User> findByUserName(String username);

}
