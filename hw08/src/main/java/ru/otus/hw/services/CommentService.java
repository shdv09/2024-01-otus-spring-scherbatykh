package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.Optional;

public interface CommentService {
    CommentDto create(String bookId, String commentText);

    Optional<CommentDto> findById(String id);

    CommentDto update(String id, String text);
}
