package ru.otus.hw.repositories;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(long id);
}
