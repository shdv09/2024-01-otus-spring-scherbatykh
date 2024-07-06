package ru.otus.hw.controllers.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.mappers.BookMapper;
import ru.otus.hw.dto.mappers.CommentMapper;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BookRestController {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final CommentMapper commentMapper;

    @GetMapping("/book")
    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAll(Sort.by(Sort.Order.asc("title")))
                .map(bookMapper::toDto);
    }

    @PostMapping("/book")
    public Mono<BookDto> addBook(@RequestBody @Valid BookCreateDto bookDto) {
        Mono<Author> authorMono = authorRepository.findById(bookDto.getAuthorId())
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Author with id %s not found".formatted(bookDto.getAuthorId())))));
        Mono<List<Genre>> genresMono = genreRepository.findAllById(bookDto.getGenreIds())
                .collectList()
                .flatMap(list -> {
                    if (list.size() != bookDto.getGenreIds().size()) {
                        return Mono.create(emitter -> emitter.error(
                                new NotFoundException("Not all genres found from list %s"
                                        .formatted(bookDto.getGenreIds()))));
                    } else {
                        return Mono.just(list);
                    }
                });

        return Mono.zip(Mono.just(bookDto), authorMono, genresMono)
                .map(t -> bookMapper.fromDto(t.getT1(), t.getT2(), t.getT3()))
                .flatMap(bookRepository::save)
                .map(bookMapper::toDto);
    }

    @PutMapping("/book/{id}")
    public Mono<BookDto> editBook(@RequestBody @Valid BookUpdateDto bookDto) {
        Mono<Author> authorMono = authorRepository.findById(bookDto.getAuthorId())
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Author with id %s not found".formatted(bookDto.getAuthorId())))));
        Mono<List<Genre>> genresMono = genreRepository.findAllById(bookDto.getGenreIds())
                .collectList()
                .flatMap(list -> {
                    if (list.size() != bookDto.getGenreIds().size()) {
                        return Mono.create(emitter -> emitter.error(
                                new NotFoundException("Not all genres found from list %s"
                                        .formatted(bookDto.getGenreIds()))));
                    } else {
                        return Mono.just(list);
                    }
                });
        Mono<BookUpdateDto> bookDtoMono = bookRepository.findById(bookDto.getId())
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Book with id %s not found".formatted(bookDto.getId())))))
                .flatMap(b -> Mono.just(bookDto));

        return Mono.zip(bookDtoMono, authorMono, genresMono)
                .map(t -> bookMapper.fromDto(t.getT1(), t.getT2(), t.getT3()))
                .flatMap(bookRepository::save)
                .map(bookMapper::toDto);
    }

    @GetMapping("/book/{id}")
    public Mono<BookDto> findBook(@PathVariable(name = "id") String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Book with id %s not found".formatted(bookId)))))
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

    @DeleteMapping("/book/{id}")
    public Mono<Void> deleteBook(@PathVariable(name = "id") String id) {
        return commentRepository.deleteByBookId(id)
                .and(bookRepository.deleteById(id));
    }

    @GetMapping("/book/{id}/comment")
    public Flux<CommentDto> getCommentsForBook(@PathVariable(name = "id") String bookId) {
        return commentRepository.findByBookId(bookId)
                .map(commentMapper::toDto);
    }

    @PostMapping("/book/{id}/comment")
    public Mono<CommentDto> addComment(
            @PathVariable(name = "id") String bookId,
            @RequestBody @Valid CommentCreateDto commentDto) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.create(emitter -> emitter.error(
                        new NotFoundException("Book with id %s not found".formatted(bookId)))))
                .map(b -> {
                    Comment comment = new Comment();
                    comment.setText(commentDto.getText());
                    comment.setBook(b);
                    return comment;
                })
                .flatMap(commentRepository::save)
                .map(commentMapper::toDto);
    }
}
