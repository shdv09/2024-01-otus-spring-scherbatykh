package ru.otus.hw.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DicRestController {
    private final GenreService genreService;

    private final AuthorService authorService;

    @GetMapping("/dic/genres")
    public List<GenreDto> getGenresDic() {
        return genreService.findAll();
    }

    @GetMapping("/dic/authors")
    public List<AuthorDto> getAuthorsDic() {
        return authorService.findAll();
    }
}
