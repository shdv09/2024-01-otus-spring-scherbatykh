package ru.otus.hw.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.dto.response.ErrorDto;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@AutoConfigureWebTestClient
public class BookRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    private BookDto bookDto;

    private BookCreateDto bookCreateDto;

    private BookUpdateDto bookUpdateDto;

    private CommentDto commentDto;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    void init() {
        AuthorDto authorDto = new AuthorDto("1", "author");
        GenreDto genreDto = new GenreDto("2", "genre");
        bookDto = new BookDto("3", "book", authorDto, List.of(genreDto), null);
        bookCreateDto = new BookCreateDto("book", "1", Set.of("2"));
        bookUpdateDto = new BookUpdateDto("3", "book", "1", Set.of("2"));
        commentDto = new CommentDto("4", "comment text");
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookService, authorService, genreService, commentService);
    }

    @Test
    void getBookListPositiveTest() {
        Flux<BookDto> daoRes = Flux.just(this.bookDto);
        given(bookService.findAll()).willReturn(daoRes);

        webClient.get()
                .uri("/api/book")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .contains(this.bookDto);

        verify(bookService).findAll();
    }

    @Test
    void getBookListError500Test() {
        given(bookService.findAll()).willThrow(RuntimeException.class);

        webClient.get()
                .uri("/api/book")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorDto.class);

        verify(bookService).findAll();
    }

   @Test
    void saveNewBookPositiveTest() {
        given(bookService.create(any())).willReturn(Mono.just(bookDto));

       webClient.post()
               .uri("/api/book")
               .contentType(MediaType.APPLICATION_JSON)
               .body(BodyInserters.fromValue(bookCreateDto))
               .exchange()
               .expectStatus().isOk()
               .expectBody(BookDto.class)
               .isEqualTo(this.bookDto);

        verify(bookService).create(bookCreateDto);
    }

    @Test
    void saveNewBookError500Test() {
        given(bookService.create(any())).willThrow(RuntimeException.class);

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookCreateDto))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorDto.class);

        verify(bookService).create(bookCreateDto);
    }

     @Test
    void editBookPositiveTest() {
        given(bookService.update(any())).willReturn(Mono.just(bookDto));

         webClient.put()
                 .uri("/api/book/3")
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(BodyInserters.fromValue(bookUpdateDto))
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(BookDto.class)
                 .isEqualTo(this.bookDto);

        verify(bookService).update(any());
    }

    @Test
    void editBookError404Test() {
        given(bookService.update(any())).willThrow(NotFoundException.class);

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(bookService).update(any());
    }

    @Test
    void editBookError500Test() {
        given(bookService.update(any())).willThrow(RuntimeException.class);

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookService).update(any());
    }

   @Test
    void findBookPositiveTest() {
        given(bookService.findById(anyString())).willReturn(Mono.just(this.bookDto));

       webClient.get()
               .uri("/api/book/3")
               .exchange()
               .expectStatus().isOk()
               .expectBody(BookDto.class)
               .isEqualTo(this.bookDto);

        verify(bookService).findById("3");
    }

     @Test
    void findBookError404Test() {
        given(bookService.findById(anyString())).willThrow(NotFoundException.class);

         webClient.get()
                 .uri("/api/book/3")
                 .exchange()
                 .expectStatus().isNotFound();

        verify(bookService).findById("3");
    }

    @Test
    void findBookError500Test() {
        given(bookService.findById(anyString())).willThrow(RuntimeException.class);

        webClient.get()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookService).findById("3");
    }

    @Test
    void deleteBookPositiveTest() {
        given(bookService.deleteById("3")).willReturn(Mono.empty());

        webClient.delete()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().isOk();

        verify(bookService).deleteById("3");
    }

   @Test
    void deleteBookError500Test() {
        given(bookService.deleteById("3")).willThrow(new RuntimeException());

/*        mvc.perform(delete("/api/book/3"))
                .andExpect(status().isInternalServerError()).andDo(print());*/

       webClient.delete()
               .uri("/api/book/3")
               .exchange()
               .expectStatus().is5xxServerError();

        verify(bookService).deleteById("3");
    }

     @Test
    void getCommentsForBookPositiveTest() {
        List<CommentDto> daoRes = List.of(this.commentDto);
        given(commentService.findByBookId(anyString())).willReturn(Flux.fromIterable(daoRes));

         webClient.get()
                 .uri("/api/book/3/comment")
                 .exchange()
                 .expectStatus().isOk()
                 .expectBodyList(CommentDto.class)
                 .hasSize(1)
                 .contains(this.commentDto);

        verify(commentService).findByBookId("3");
    }

   @Test
    void getCommentsForBookError500Test() {
        given(commentService.findByBookId(anyString())).willThrow(RuntimeException.class);

       webClient.get()
               .uri("/api/book/3/comment")
               .exchange()
               .expectStatus().is5xxServerError()
               .expectBody(ErrorDto.class);

        verify(commentService).findByBookId("3");
    }

    @Test
    void addNewCommentForBookPositiveTest() {
        given(commentService.create(anyString(), anyString())).willReturn(Mono.just(commentDto));
        CommentCreateDto request = new CommentCreateDto("comment text");

        webClient.post()
                .uri("/api/book/3/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(this.commentDto);

        verify(commentService).create("3", request.getText());
    }

     @Test
    void addNewCommentForBookError404Test() {
        given(commentService.create(anyString(), anyString())).willThrow(NotFoundException.class);
        CommentCreateDto request = new CommentCreateDto("comment text");

         webClient.post()
                 .uri("/api/book/3/comment")
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(BodyInserters.fromValue(request))
                 .exchange()
                 .expectStatus().isNotFound();

        verify(commentService).create("3", request.getText());
    }

    @Test
    void addNewCommentForBookError500Test() {
        given(commentService.create(anyString(), anyString())).willThrow(RuntimeException.class);
        CommentCreateDto request = new CommentCreateDto("comment text");

        webClient.post()
                .uri("/api/book/3/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().is5xxServerError();

        verify(commentService).create("3", request.getText());
    }
}
