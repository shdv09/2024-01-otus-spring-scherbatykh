package ru.otus.hw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.dto.response.GenreDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookCreateDto {
    private long id;

    @NotBlank(message = "Title should not be blank")
    @Size(min = 2, max = 20, message = "Title should have expected size")
    private String title;

    @NotNull(message = "Author should not be null")
    private AuthorDto author;

    @NotEmpty(message = "Genres should not be empty")
    private Set<GenreDto> genres;

    private List<CommentDto> comments = new ArrayList<>();
}
