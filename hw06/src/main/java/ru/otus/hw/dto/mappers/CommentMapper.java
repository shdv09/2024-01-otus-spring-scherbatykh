package ru.otus.hw.dto.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setText(comment.getText());
        return result;
    }

    public Comment toModel(CommentDto dto) {
        if (dto == null) {
            return null;
        }
        Comment result = new Comment();
        result.setId(dto.getId());
        result.setText(dto.getText());
        return result;
    }
}
