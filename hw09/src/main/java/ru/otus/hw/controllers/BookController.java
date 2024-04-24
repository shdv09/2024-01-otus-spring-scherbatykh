package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "booksList";
    }

    @GetMapping("/book/{id}")
    public String editPage(@PathVariable(name = "id") long id, Model model) {
        BookDto book = bookService.findById(id);
        model.addAttribute("book", book);
        addDictToModel(model);
        return "bookEdit";
    }

    @GetMapping("/book")
    public String createPage(Model model) {
        model.addAttribute("book", new BookDto());
        addDictToModel(model);
        return "bookEdit";
    }

    @PostMapping("/book")
    public String addBook(@Valid @ModelAttribute("book") BookCreateDto book,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            addDictToModel(model);
            return "bookEdit";
        }
        bookService.create(book);
        return "redirect:/";
    }

    @PostMapping("/book/{id}")
    public String editBook(@PathVariable(name = "id") long id,
                           @Valid @ModelAttribute("book") BookUpdateDto book,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            addDictToModel(model);
            return "bookEdit";
        }
        bookService.update(book);
        return "redirect:/";
    }

    @PostMapping("book/{id}/delete")
    public String deleteBook(@PathVariable(name = "id") long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    private void addDictToModel(Model model) {
        model.addAttribute("allAuthors", authorService.findAll());
        model.addAttribute("allGenres", genreService.findAll());
    }
}
