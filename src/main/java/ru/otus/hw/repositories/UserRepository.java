package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph("user-authorities-entity-graph")
    User findByUsername(String username);
}
