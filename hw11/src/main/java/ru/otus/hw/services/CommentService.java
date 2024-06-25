package ru.otus.hw.services;

import ru.otus.hw.dto.response.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(String bookId, String commentText);

    CommentDto findById(String id);

    List<CommentDto> findByBookId(String bookId);
}
