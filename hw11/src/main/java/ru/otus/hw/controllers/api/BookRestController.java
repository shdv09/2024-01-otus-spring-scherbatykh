package ru.otus.hw.controllers.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BookRestController {
    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping("/book")
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @PostMapping("/book")
    public Mono<BookDto> addBook(@RequestBody @Valid BookCreateDto book) {
        return bookService.create(book);
    }

    @PutMapping("/book/{id}")
    public Mono<BookDto> editBook(@RequestBody @Valid BookUpdateDto book) {
        return bookService.update(book);
    }

    @GetMapping("/book/{id}")
    public Mono<BookDto> findBook(@PathVariable(name = "id") String id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/book/{id}")
    public Mono<Void> deleteBook(@PathVariable(name = "id") String id) {
        return bookService.deleteById(id);
    }

    @GetMapping("/book/{id}/comment")
    public Flux<CommentDto> getCommentsForBook(@PathVariable(name = "id") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/book/{id}/comment")
    public Mono<CommentDto> addComment(@PathVariable(name = "id") String bookId, @RequestBody @Valid CommentCreateDto comment) {
        return commentService.create(bookId, comment.getText());
    }
}
