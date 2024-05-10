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
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @PostMapping()
    public BookDto addBook(@RequestBody @Valid BookCreateDto book) {
        return bookService.create(book);
    }

    @PutMapping()
    public BookDto editBook(@RequestBody @Valid BookUpdateDto book) {
        return bookService.update(book);
    }

    @GetMapping("/{id}")
    public BookDto findBook(@PathVariable(name = "id") long id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable(name = "id") long id) {
        bookService.deleteById(id);
    }
}
