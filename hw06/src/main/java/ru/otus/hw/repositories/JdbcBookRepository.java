package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcBookRepository implements BookRepository {

    private static final String SQL_DELETE = "DELETE FROM BOOKS WHERE id = :id";

    private static final String SQL_INSERT_RELATIONS = "INSERT INTO books_genres (book_id, genre_id) VALUES (?, ?)";

    private static final String SQL_INSERT_BOOK = """
            INSERT INTO books (title, author_id)
            VALUES
              (:title, :authorId)
            """;

    private static final String SQL_DELETE_RELATIONS = "DELETE FROM books_genres WHERE book_id = :id";

    private static final String SQL_UPDATE = """
            UPDATE
              books
            SET
              title = :title,
              author_id = :authorId
            WHERE
              id = :id
            """;

    private static final String ERROR_MESSAGE = "Error updating book with id = %d. Row not found";

    private final NamedParameterJdbcTemplate jdbcTemplate;

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
        jdbcTemplate.update(SQL_DELETE, Map.of("id", id));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("title", book.getTitle());
        parameters.addValue("authorId", book.getAuthor().getId());
        jdbcTemplate.update(SQL_INSERT_BOOK, parameters, keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", book.getId());
        parameters.addValue("title", book.getTitle());
        parameters.addValue("authorId", book.getAuthor().getId());
        int updatedRowCount = jdbcTemplate.update(SQL_UPDATE, parameters);
        if (updatedRowCount == 0) {
            throw new EntityNotFoundException(String.format(ERROR_MESSAGE, book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Genre> genres = book.getGenres();
        jdbcTemplate.getJdbcTemplate().batchUpdate(SQL_INSERT_RELATIONS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, book.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void removeGenresRelationsFor(Book book) {
        jdbcTemplate.update(SQL_DELETE_RELATIONS, Map.of("id", book.getId()));
    }
}