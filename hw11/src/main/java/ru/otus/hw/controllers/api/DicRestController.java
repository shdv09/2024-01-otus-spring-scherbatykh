package ru.otus.hw.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.mappers.AuthorMapper;
import ru.otus.hw.dto.mappers.GenreMapper;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DicRestController {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    private final AuthorMapper authorMapper;

    @GetMapping("/dic/genres")
    public Flux<GenreDto> getGenresDic() {
        return genreRepository.findAll()
                .map(genreMapper::toDto);
    }

    @GetMapping("/dic/authors")
    public Flux<AuthorDto> getAuthorsDic() {
        return authorRepository.findAll()
                .map(authorMapper::toDto);
    }
}
