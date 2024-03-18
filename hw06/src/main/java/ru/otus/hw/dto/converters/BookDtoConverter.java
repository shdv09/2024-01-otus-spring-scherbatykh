package ru.otus.hw.dto.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookDtoConverter {
    private final AuthorDtoConverter authorDtoConverter;

    private final GenreDtoConverter genreDtoConverter;

    private final CommentDtoConverter commentDtoConverter;

    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        BookDto result = new BookDto();
        result.setId(book.getId());
        result.setTitle(book.getTitle());
        result.setAuthor(authorDtoConverter.toDto(book.getAuthor()));
        result.setGenres(book.getGenres().stream()
                .map(genreDtoConverter::toDto)
                .toList());
        result.setComments(book.getComments().stream()
                .map(commentDtoConverter::toDto)
                .collect(Collectors.toList()));
        return result;
    }

    public Book fromDto(BookDto dto) {
        if (dto == null) {
            return null;
        }
        Book result = new Book();
        result.setId(dto.getId());
        result.setTitle(dto.getTitle());
        result.setAuthor(authorDtoConverter.fromDto(dto.getAuthor()));
        result.setGenres(dto.getGenres().stream()
                .map(genreDtoConverter::fromDto)
                .toList());
        result.setComments(dto.getComments().stream()
                .map(commentDtoConverter::fromDto)
                .collect(Collectors.toList()));
        return result;
    }
}
