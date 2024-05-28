package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.models.Book;

@Service
public class BookMongoToSqlTransformer {
    public Book transform(Book book) {
        System.out.println(book);
        return book;
    }
}
