package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public List<CommentDto> addComment(long bookId, String commentText) {
        BookDto bookDto = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(bookId)));
        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);
        bookDto.getComments().add(commentDto);
        bookRepository.save(bookDto);
        return commentRepository.findByBookId(bookDto.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id);
    }
}
