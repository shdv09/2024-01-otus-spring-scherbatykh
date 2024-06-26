package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.mappers.BookMapper;
import ru.otus.hw.dto.mappers.CommentMapper;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .map(this::loadComments);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Order.asc("title")))
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<BookDto> create(BookCreateDto dto) {
        var author = findAuthor(dto.getAuthorId());
        var genres = findGenres(dto.getGenreIds());
        Book book = bookMapper.fromDto(dto, author, genres);
        return bookRepository.save(book)
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    @SuppressWarnings("java:S2201")
    public Mono<BookDto> update(BookUpdateDto dto) {
        Mono<BookDto> bookExists = bookRepository.findById(dto.getId())
                .map(bookMapper::toDto)
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Book with id %s not found".formatted(dto.getId())))))
                .ignoreElement();
        var author = findAuthor(dto.getAuthorId());
        var genres = findGenres(dto.getGenreIds());
        Book book = bookMapper.fromDto(dto, author, genres);
        Mono<BookDto> saveBook = bookRepository.save(book)
                .map(bookMapper::toDto);
        return bookExists.or(saveBook);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteByBookId(id)
                .and(bookRepository.deleteById(id));
    }

    private BookDto loadComments(BookDto dto) {
        dto.setComments(commentRepository.findByBookId(dto.getId())
                .map(commentMapper::toDto)
                .toStream()
                .toList()
        );
        return dto;
    }

    private Author findAuthor(String id) {
        return authorRepository.findById(id)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Author with id %s not found".formatted(id)));
    }

    private List<Genre> findGenres(Set<String> genreIds) {
        var genres = genreRepository.findAllById(genreIds)
                .toStream()
                .toList();
        if (genres.size() != genreIds.size()) {
            throw new NotFoundException("Not all genres found from list %s".formatted(genreIds));
        }
        return genres;
    }
}
