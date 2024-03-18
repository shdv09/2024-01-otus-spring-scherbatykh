package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.converters.CommentDtoConverter;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final CommentDtoConverter commentDtoConverter;

    @Override
    public List<CommentDto> findByBookId(long bookId) {
        TypedQuery<Comment> query = entityManager.createQuery(
                "select c from Book b join b.comments c where b.id = :bookId",
                Comment.class);
        query.setParameter("bookId", bookId);
        List<Comment> comments = query.getResultList();
        return comments.stream()
                .map(commentDtoConverter::toDto)
                .toList();
    }

    @Override
    public Optional<CommentDto> findById(long id) {
        Comment comment = entityManager.find(Comment.class, id);
        return Optional.ofNullable(commentDtoConverter.toDto(comment));
    }
}
