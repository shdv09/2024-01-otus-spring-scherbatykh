package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {

    private long id;

    @NotBlank(message = "Comment text should not be blank")
    @Size(min = 2, max = 20, message = "Comment text should have expected size")
    private String text;
}
