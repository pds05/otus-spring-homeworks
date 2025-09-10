package ru.otus.hw.services;

import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);
}
