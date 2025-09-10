package ru.otus.hw.repositories;

import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    void deleteById(long id);

}
