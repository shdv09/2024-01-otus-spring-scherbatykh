package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookRepository JpaBookrepository;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Book expectedBook = em.find(Book.class, 1);
        var actualBook = JpaBookrepository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = JpaBookrepository.findAll();

        assertEquals(3, actualBooks.size());
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = em.find(Author.class, 2);
        Genre genre4 = em.find(Genre.class, 4);
        Genre genre5 = em.find(Genre.class, 5);
        var expectedBook =
                new Book(0L, "Title4", author, List.of(genre4, genre5), Collections.emptyList());

        var returnedBook = JpaBookrepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringFields("id").ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(JpaBookrepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Author author = em.find(Author.class, 2);
        Genre genre4 = em.find(Genre.class, 4);
        Genre genre5 = em.find(Genre.class, 5);
        Comment comment = new Comment(0L, "text1");
        var expectedBook =
                new Book(1L, "BookTitle_10500", author, List.of(genre4, genre5), List.of(comment));

        assertThat(JpaBookrepository.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = JpaBookrepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .matches(book -> book.getComments().get(0).getId() > 0)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(CommentDto::getText), CommentDto.class)
                .ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(JpaBookrepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(JpaBookrepository.findById(1L)).isPresent();
        JpaBookrepository.deleteById(1L);
        assertThat(JpaBookrepository.findById(1L)).isEmpty();
    }
}