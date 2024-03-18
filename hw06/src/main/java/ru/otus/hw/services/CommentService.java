package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> addComment(long bookId, String commentText);

    Optional<CommentDto> findById(long id);
}
