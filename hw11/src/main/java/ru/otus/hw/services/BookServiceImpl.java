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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

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
                .flatMap(b -> commentRepository.findByBookId(b.getId())
                        .map(commentMapper::toDto)
                        .collectList()
                        .map(c -> {
                            b.setComments(c);
                            return b;
                        })
                );
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
        Mono<Author> authorMono = authorRepository.findById(dto.getAuthorId());
        Mono<List<Genre>> genresMono = genreRepository.findAllById(dto.getGenreIds())
                .collectList();

        return Mono.zip(Mono.just(dto), authorMono, genresMono)
                .map(t -> bookMapper.fromDto(t.getT1(), t.getT2(), t.getT3()))
                .flatMap(bookRepository::save)
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    @SuppressWarnings("java:S2201")
    public Mono<BookDto> update(BookUpdateDto dto) {
        Mono<Author> authorMono = authorRepository.findById(dto.getAuthorId());
        Mono<List<Genre>> genresMono = genreRepository.findAllById(dto.getGenreIds())
                .collectList();

        return Mono.zip(Mono.just(dto), authorMono, genresMono)
                .map(t -> bookMapper.fromDto(t.getT1(), t.getT2(), t.getT3()))
                .flatMap(bookRepository::save)
                .map(bookMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteByBookId(id)
                .and(bookRepository.deleteById(id));
    }
}
