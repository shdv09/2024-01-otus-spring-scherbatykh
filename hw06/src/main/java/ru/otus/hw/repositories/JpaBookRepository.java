package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(entityManager.find(Book.class, id));
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("select b from Book b", Book.class).getResultList();
    }

    @Override
    public Book save(Book book) {
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

    private Book insert(Book book) {
        entityManager.persist(book);
        return book;
    }

    private Book update(Book book) {
        return entityManager.merge(book);
    }
}