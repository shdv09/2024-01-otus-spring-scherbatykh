package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    //ac 1 goodBook
    @ShellMethod(value = "Add comment", key = "ac")
    String addComment(long bookId, String commentText) {
        List<CommentDto> comments = commentService.addComment(bookId, commentText);
        return comments.stream()
                .map(commentConverter::commentToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
    }

    //cbid 1
    @ShellMethod(value = "Find comment by id", key = "cbid")
    String getComment(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d not found".formatted(id)));
    }
}
