package ru.otus.hw.services;

import ru.otus.hw.dto.response.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(long bookId, String commentText);

    CommentDto findById(long id);

    List<CommentDto> findByBookId(long bookId);
}
