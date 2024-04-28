package ru.otus.hw.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookUpdateDto {
    @Min(value = 1, message = "Book id should have positive value")
    private long id;

    @NotBlank(message = "Title should not be blank")
    @Size(min = 2, max = 20, message = "Title should have expected size")
    private String title;

    @NotNull(message = "Author should not be null")
    private Long author;

    @NotEmpty(message = "Genres should not be empty")
    @NotNull(message = "Genres should not be null")
    private Set<Long> genres;
}
