package ru.otus.hw.services;

import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findAll();

    BookDto create(BookCreateDto dto);

    BookDto update(BookUpdateDto dto);

    void deleteById(long id);
}
