package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.response.CommentDto;

public interface CommentService {
    Mono<CommentDto> create(String bookId, String commentText);

    Mono<CommentDto> findById(String id);

    Flux<CommentDto> findByBookId(String bookId);
}
