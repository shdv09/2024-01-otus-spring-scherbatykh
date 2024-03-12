package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class JdbcGenreRepository implements GenreRepository {
    private static final String FIND_ALL_SQL = "SELECT id, name FROM genres";

    private static final String FIND_BY_ID_SQL = "SELECT id, name FROM genres WHERE id = :id";

    private static final String FIND_BY_IDS_SQL = "SELECT id, name FROM genres WHERE id IN (:ids)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return jdbcTemplate.query(FIND_BY_IDS_SQL, Map.of("ids", ids), new GenreRowMapper());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
