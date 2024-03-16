package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class JpaGenreRepository implements GenreRepository {

    private final EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("select g from Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        TypedQuery<Genre> query =
                entityManager.createQuery("select g from Genre g where g.id in :ids", Genre.class);
        return query.setParameter("ids", ids).getResultList();
    }
}
