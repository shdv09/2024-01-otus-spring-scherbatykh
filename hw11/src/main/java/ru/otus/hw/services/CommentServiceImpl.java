package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.dto.mappers.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public Mono<CommentDto> create(String bookId, String commentText) {
        return bookRepository.findById(bookId)
                .map(b -> {
                    Comment comment = new Comment();
                    comment.setText(commentText);
                    comment.setBook(b);
                    return comment;
                })
                .flatMap(commentRepository::save)
                .map(commentMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
                .map(commentMapper::toDto);
    }
}
