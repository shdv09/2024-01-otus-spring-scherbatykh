package ru.otus.hw.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.auth.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String userName);
}
