package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcBookRepository implements BookRepository {

    private static final String SQL_FIND_ALL = """
            SELECT
              books.id AS book_id,
              books.title AS book_title,
              authors.id AS author_id,
              authors.full_name AS author_full_name,
              genres.id AS genre_id,
              genres.name AS genre_name
            FROM
              books
              INNER JOIN authors ON authors.id = books.author_id
              INNER JOIN genres ON genres.id = books.genre_id
            """;

    private static final String SQL_FIND_BY_ID_SUFFIX = " WHERE books.id = :id";

    private static final String SQL_DELETE = "DELETE FROM BOOKS WHERE id = :id";

    private static final String SQL_INSERT = """
            INSERT INTO books (title, author_id, genre_id)
            VALUES
              (:title, :authorId, :genreId)
            """;

    private static final String SQL_UPDATE = """
            UPDATE
              books
            SET
              title = :title,
              author_id = :authorId,
              genre_id = :genreId
            WHERE
              id = :id
            """;

    private static final String ERROR_MESSAGE = "Error updating book with id = %d. Row not found";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        return jdbcTemplate.query(SQL_FIND_ALL + SQL_FIND_BY_ID_SUFFIX, Map.of("id", id), new BookRowMapper())
                .stream()
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, new BookRowMapper());
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
        parameters.addValue("genreId", book.getGenre().getId());
        jdbcTemplate.update(SQL_INSERT, parameters, keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", book.getId());
        parameters.addValue("title", book.getTitle());
        parameters.addValue("authorId", book.getAuthor().getId());
        parameters.addValue("genreId", book.getGenre().getId());
        int updatedRowCount = jdbcTemplate.update(SQL_UPDATE, parameters);
        if (updatedRowCount == 0) {
            throw new EntityNotFoundException(String.format(ERROR_MESSAGE, book.getId()));
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            long bookId = rs.getLong("book_id");
            String bookTitle = rs.getString("book_title");
            return new Book(bookId, bookTitle, author, genre);
        }
    }
}
