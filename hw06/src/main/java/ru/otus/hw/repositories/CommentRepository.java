package ru.otus.hw.repositories;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<CommentDto> findByBookId(long bookId);

    Optional<CommentDto> findById(long id);
}
