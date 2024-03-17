package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.converters.BookDtoConverter;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final BookDtoConverter bookDtoConverter;

    @Override
    public Optional<BookDto> findById(long id) {
        Book book = entityManager.find(Book.class, id);
        return Optional.ofNullable(bookDtoConverter.toDto(book));
    }

    @Override
    public List<BookDto> findAll() {
        List<Book> books = entityManager.createQuery("select b from Book b join fetch b.author", Book.class)
                .getResultList();
        return books.stream()
                .map(bookDtoConverter::toDto)
                .toList();
    }

    @Override
    public BookDto save(BookDto bookDto) {
        Book book = bookDtoConverter.fromDto(bookDto);
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = entityManager.find(Book.class, id);
        if (book == null) {
            return;
        }
        entityManager.remove(book);
    }

    private BookDto insert(Book book) {
        entityManager.persist(book);
        return bookDtoConverter.toDto(book);
    }

    private BookDto update(Book book) {
        return bookDtoConverter.toDto(entityManager.merge(book));
    }
}