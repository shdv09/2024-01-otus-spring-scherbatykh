package ru.otus.hw.dto.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto toDto(Book book) {
        BookDto result = new BookDto();
        result.setId(book.getId());
        result.setTitle(book.getTitle());
        result.setAuthor(authorMapper.toDto(book.getAuthor()));
        result.setGenres(book.getGenres().stream()
                .map(genreMapper::toDto)
                .toList());
        return result;
    }

    public Book fromDto(BookUpdateDto dto) {
        Book result = new Book();
        result.setId(dto.getId());
        result.setTitle(dto.getTitle());
        result.setAuthor(authorMapper.fromDto(dto.getAuthor()));
        result.setGenres(dto.getGenres().stream()
                .map(genreMapper::fromDto)
                .collect(Collectors.toList()));
        return result;
    }

    public Book fromDto(BookCreateDto dto) {
        Book result = new Book();
        result.setTitle(dto.getTitle());
        result.setAuthor(authorMapper.fromDto(dto.getAuthor()));
        result.setGenres(dto.getGenres().stream()
                .map(genreMapper::fromDto)
                .collect(Collectors.toList()));
        return result;
    }
}
