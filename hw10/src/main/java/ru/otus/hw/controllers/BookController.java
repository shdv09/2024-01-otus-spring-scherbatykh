package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String listPage() {
        return "booksList";
    }

    @GetMapping("/book/{id}")
    public String editPage(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("id", id);
        addDictToModel(model);
        return "bookEdit";
    }

    @GetMapping("/book")
    public String createPage(Model model) {
        addDictToModel(model);
        return "bookEdit";
    }

    private void addDictToModel(Model model) {
        model.addAttribute("allAuthors", authorService.findAll());
        model.addAttribute("allGenres", genreService.findAll());
    }
}
