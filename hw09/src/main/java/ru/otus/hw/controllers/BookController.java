package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @ModelAttribute("allAuthors")
    public List<AuthorDto> populateAuthors() {
        return authorService.findAll();
    }

    @ModelAttribute("allGenres")
    public List<GenreDto> populateGenres() {
        return genreService.findAll();
    }

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
    public String saveBook(@Valid @ModelAttribute("book") BookDto book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "bookEdit";
        }
        Set<Long> genreIds = book.getGenres().stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet());
        if (book.getId() == 0) {
            bookService.create(book.getTitle(), book.getAuthor().getId(), genreIds);
        } else {
            bookService.update(book.getId(), book.getTitle(), book.getAuthor().getId(), genreIds);
        }
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deletePage(@RequestParam(name = "id") long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(id)));
        model.addAttribute("book", book);
        return "bookDelete";
    }

    @PostMapping("/delete")
    public String deleteBook(BookDto book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "bookDelete";
        }
        bookService.deleteById(book.getId());
        return "redirect:/";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("book", new BookDto());
        return "bookEdit";
    }

    @GetMapping("/addComment")
    public String addCommentPage(@RequestParam(name = "id") long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(id)));
        model.addAttribute("book", book);
        model.addAttribute("comment", new CommentDto());
        return "addComment";
    }

    @PostMapping("/addComment")
    public String addComment(@ModelAttribute("book") BookDto book, @Valid @ModelAttribute("comment") CommentDto comment, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "addComment";
        }
        commentService.create(book.getId(), comment.getText());
        BookDto bookDto = bookService.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(book.getId())));
        model.addAttribute("book", bookDto);
        return "bookEdit";
    }
}
