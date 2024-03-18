package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.converters.AuthorDtoConverter;
import ru.otus.hw.dto.converters.CommentDtoConverter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий на основе Jpa для работы с авторами ")
@DataJpaTest
@Import({JpaAuthorRepository.class, AuthorDtoConverter.class})
class JpaAuthorRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        Book book = em.find(Book.class, 1L);
        Author expectedAuthor = book.getAuthor();

        var actualAuthor = jpaAuthorRepository.findById(expectedAuthor.getId());

        assertThat(actualAuthor).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        List<AuthorDto> authors = jpaAuthorRepository.findAll();

        assertEquals(3, authors.size());
        authors.forEach(System.out::println);
    }
}