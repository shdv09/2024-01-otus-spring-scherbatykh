package ru.otus.hw.services;

import ru.otus.hw.dto.response.CommentDto;

public interface CommentService {
    CommentDto create(long bookId, String commentText);

    CommentDto findById(long id);

    CommentDto update(long id, String text);
}
