package ru.otus.hw.repositories;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    BookDto save(BookDto book);

    void deleteById(long id);
}
