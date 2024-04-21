package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDto {
    private long id;

    @NotBlank(message = "Title should not be blank")
    @Size(min = 2, max = 20, message = "Title should have expected size")
    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;

    private List<CommentDto> comments = new ArrayList<>();
}
