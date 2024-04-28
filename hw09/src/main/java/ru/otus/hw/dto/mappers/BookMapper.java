package ru.otus.hw.dto.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

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
        populateModel(result, dto.getAuthor(), dto.getGenres());
        return result;
    }

    public Book fromDto(BookCreateDto dto) {
        Book result = new Book();
        result.setTitle(dto.getTitle());
        populateModel(result, dto.getAuthor(), dto.getGenres());
        return result;
    }

    private void populateModel(Book book, Long authorId, Set<Long> genreIds) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Author with id %d not found".formatted(authorId)));
        book.setAuthor(author);
        var genres = genreRepository.findAllById(genreIds);
        if (genres.size() != genreIds.size()) {
            throw new NotFoundException("Not all genres found from list %s".formatted(genreIds));
        }
        book.setGenres(genres);
    }
}
