package ru.otus.hw.services;

import ru.otus.hw.dto.response.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(String authorId);
}
