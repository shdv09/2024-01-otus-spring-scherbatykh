package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JdbcBookRepository implements BookRepository {

    private static final String SQL_GET_GENRE_RELATIONS = "SELECT book_id, genre_id FROM books_genres";

    private static final String SQL_GET_ALL_BOOKS_WITHOUT_GENRES = """
            SELECT
              books.id AS book_id,
              books.title AS book_title,
              authors.id AS author_id,
              authors.full_name AS author_full_name
            FROM
              books
              INNER JOIN authors ON authors.id = books.author_id
            """;

    private static final String SQL_GET_BY_ID = """
            SELECT
              books.id AS book_id,
              books.title AS book_title,
              authors.id AS author_id,
              authors.full_name AS author_full_name,
              genres.id AS genre_id,
              genres.name AS genre_name
            FROM
              BOOKS
              INNER JOIN authors ON books.author_id = authors.id
              INNER JOIN books_genres bg ON books.id = bg.book_id
              INNER JOIN genres ON bg.genre_id = genres.id
            WHERE
              books.id = :id
            """;

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

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(jdbcTemplate.query(SQL_GET_BY_ID, Map.of("id", id), new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
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

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcTemplate.query(SQL_GET_ALL_BOOKS_WITHOUT_GENRES, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcTemplate.query(SQL_GET_GENRE_RELATIONS, new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genreDict = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, Book> booksDict = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, Function.identity()));
        relations.forEach(rel -> {
            if (booksDict.containsKey(rel.bookId()) && genreDict.containsKey(rel.genreId())) {
                booksDict.get(rel.bookId).getGenres().add(genreDict.get(rel.genreId()));
            }
        });
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

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
            long bookId = rs.getLong("book_id");
            String bookTitle = rs.getString("book_title");
            return new Book(bookId, bookTitle, author, new LinkedList<>());
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book result = null;
            while (rs.next()) {
                long bookId = rs.getLong("book_id");
                String bookTitle = rs.getString("book_title");
                long authorId = rs.getLong("author_id");
                String authorFullName = rs.getString("author_full_name");
                long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");
                if (result == null) {
                    result = new Book(
                            bookId,
                            bookTitle,
                            new Author(authorId, authorFullName),
                            new LinkedList<>()
                    );
                }
                result.getGenres().add(new Genre(genreId, genreName));
            }
            return result;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {
        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }
}