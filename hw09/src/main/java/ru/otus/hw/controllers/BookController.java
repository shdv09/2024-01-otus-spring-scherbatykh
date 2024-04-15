package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class BookController {
    private final BookService bookService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "booksList";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam(name = "id") long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(id)));
        model.addAttribute("book", book);
        return "bookEdit";
    }

    @PostMapping("/edit")
    public String saveBook(BookDto book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "editBook";
        }
        bookService.update(book.getId(), book.getTitle(), 1L, Set.of(1L, 2L, 3L));
        return "redirect:/";
    }
}
