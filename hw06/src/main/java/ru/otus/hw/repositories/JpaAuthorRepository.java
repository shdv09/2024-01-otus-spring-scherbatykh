package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.converters.AuthorDtoConverter;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final AuthorDtoConverter authorDtoConverter;

    @Override
    public List<AuthorDto> findAll() {
        List<Author> authors = entityManager.createQuery("select a from Author a", Author.class).getResultList();
        return authors.stream()
                .map(authorDtoConverter::toDto)
                .toList();
    }

    @Override
    public Optional<AuthorDto> findById(long id) {
        Author author = entityManager.find(Author.class, id);
        return Optional.ofNullable(authorDtoConverter.toDto(author));
    }
}
