package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcAuthorRepository implements AuthorRepository {
    private static final String FIND_ALL_SQL = "SELECT id, full_name FROM authors";

    private static final String FIND_BY_ID_SQL = "SELECT id, full_name FROM authors WHERE id = :id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        return jdbcTemplate.query(FIND_BY_ID_SQL, Map.of("id", id), new AuthorRowMapper()).stream()
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("full_name");
            return new Author(id, name);
        }
    }
}
