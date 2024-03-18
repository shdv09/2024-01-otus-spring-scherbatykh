package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.converters.AuthorDtoConverter;
import ru.otus.hw.dto.converters.BookDtoConverter;
import ru.otus.hw.dto.converters.CommentDtoConverter;
import ru.otus.hw.dto.converters.GenreDtoConverter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@Import({JpaCommentRepository.class, CommentDtoConverter.class})
class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        Book book = em.find(Book.class, 1L);
        Comment expectedComment = book.getComments().get(0);

        var actualComment = jpaCommentRepository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список комментариев для книги")
    @Test
    void shouldReturnCorrectCommentsListForBook() {
        Book book = em.find(Book.class, 1L);
        List<Comment> expectedComments = book.getComments();

        List<CommentDto> actualComments = jpaCommentRepository.findByBookId(book.getId());

        assertThat(actualComments).usingRecursiveComparison().isEqualTo(expectedComments);
    }
}