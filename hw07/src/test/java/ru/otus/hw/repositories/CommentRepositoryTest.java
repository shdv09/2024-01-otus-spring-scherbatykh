package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе SpringDataJpa для работы с комментариями ")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        Book book = em.find(Book.class, 1L);
        Comment expectedComment = book.getComments().get(0);

        var actualComment = commentRepository.findById(expectedComment.getId());

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

        List<Comment> actualComments = commentRepository.findByBookId(book.getId());

        assertThat(actualComments).usingRecursiveComparison().isEqualTo(expectedComments);
    }

    @DisplayName("должен изменять текст комментария")
    @Test
    void shouldUpdateCommentById() {
        Comment comment = em.find(Comment.class, 1L);
        comment.setText("updated text");

        var actualComment = commentRepository.save(comment);

        assertThat(actualComment)
                .usingRecursiveComparison()
                .isEqualTo(comment);
    }
}