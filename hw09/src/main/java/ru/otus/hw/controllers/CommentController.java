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
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/addComment")
    public String addCommentPage(@RequestParam(name = "id") long id, Model model) {
        BookDto book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("comment", new CommentDto());
        return "addComment";
    }

    @PostMapping("/addComment")
    public String addComment(@ModelAttribute("book") BookDto book,
                             @Valid @ModelAttribute("comment") CommentCreateDto comment,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "addComment";
        }
        commentService.create(book.getId(), comment.getText());
        BookDto bookDto = bookService.findById(book.getId());
        model.addAttribute("book", bookDto);
        model.addAttribute("allAuthors", authorService.findAll());
        model.addAttribute("allGenres", genreService.findAll());
        return "bookEdit";
    }
}
