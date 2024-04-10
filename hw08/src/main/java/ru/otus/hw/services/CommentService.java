package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> create(String bookId, String commentText);

    Optional<CommentDto> findById(String id);

    CommentDto update(String id, String text);
}