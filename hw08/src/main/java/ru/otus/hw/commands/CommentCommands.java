package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    //ac 1 goodBook
    @ShellMethod(value = "Add comment", key = "ac")
    String addComment(String bookId, String commentText) {
        CommentDto comment = commentService.create(bookId, commentText);
        return commentConverter.commentToString(comment);
    }

    //cbid 1
    @ShellMethod(value = "Find comment by id", key = "cbid")
    String getComment(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %s not found".formatted(id)));
    }

    //cupd 1 likeIt
    @ShellMethod(value = "Update comment", key = "cupd")
    String updateComment(String id, String commentText) {
        CommentDto comment = commentService.update(id, commentText);
        return commentConverter.commentToString(comment);
    }
}
