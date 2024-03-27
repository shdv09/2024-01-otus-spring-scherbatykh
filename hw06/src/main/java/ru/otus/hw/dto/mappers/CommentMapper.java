package ru.otus.hw.dto.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setText(comment.getText());
        return result;
    }

    public Comment toModel(CommentDto dto) {
        Comment result = new Comment();
        result.setId(dto.getId());
        result.setText(dto.getText());
        return result;
    }
}
