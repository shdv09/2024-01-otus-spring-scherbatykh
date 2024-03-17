package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.converters.GenreDtoConverter;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final GenreDtoConverter genreDtoConverter;

    @Override
    public List<GenreDto> findAll() {
        List<Genre> genres = entityManager.createQuery("select g from Genre g", Genre.class).getResultList();
        return genres.stream()
                .map(genreDtoConverter::toDto)
                .toList();
    }

    @Override
    public List<GenreDto> findAllByIds(Set<Long> ids) {
        TypedQuery<Genre> query =
                entityManager.createQuery("select g from Genre g where g.id in :ids", Genre.class);
        List<Genre> genres = query.setParameter("ids", ids).getResultList();
        return genres.stream()
                .map(genreDtoConverter::toDto)
                .toList();
    }
}