package ru.otus.hw.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.mappers.BookMapper;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.dto.response.ErrorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@AutoConfigureWebTestClient
public class BookRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private BookMapper bookMapper;

    private Book book;

    private BookCreateDto bookCreateDto;

    private BookUpdateDto bookUpdateDto;

    private CommentDto commentDto;

    private Comment comment;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @BeforeEach
    void init() {
        Author author = new Author("1", "author");
        Genre genre = new Genre("2", "genre");
        book = new Book("3", "book", author, List.of(genre));
        bookCreateDto = new BookCreateDto("book", "1", Set.of("2"));
        bookUpdateDto = new BookUpdateDto("3", "book", "1", Set.of("2"));
        commentDto = new CommentDto("4", "comment text");
        comment = new Comment("4", "comment text", this.book);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookRepository, commentRepository, authorRepository, genreRepository);
    }

    @Test
    void getBookListPositiveTest() {
        Flux<Book> daoRes = Flux.just(this.book);
        Mockito.when(bookRepository.findAll(any(Sort.class))).thenReturn(daoRes);

        webClient.get()
                .uri("/api/book")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .contains(bookMapper.toDto(this.book));

        verify(bookRepository).findAll(any(Sort.class));
    }

    @Test
    void getBookListError500Test() {
        given(bookRepository.findAll(any(Sort.class))).willThrow(RuntimeException.class);

        webClient.get()
                .uri("/api/book")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorDto.class);

        verify(bookRepository).findAll(any(Sort.class));
    }

    @Test
    void saveNewBookPositiveTest() {
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));
        given(bookRepository.save(any())).willReturn(Mono.just(this.book));

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(this.bookCreateDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(this.book));

        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
        verify(bookRepository).save(any());
    }

    @Test
    void saveNewBookAuthorNotFoundTest() {
        given(authorRepository.findById(anyString())).willReturn(Mono.empty());
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookCreateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
    }

    @Test
    void saveNewBookGenresNotFoundTest() {
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(Collections.emptyList()));

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookCreateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
    }

    @Test
    void saveNewBookError500Test() {
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));
        given(bookRepository.save(any())).willThrow(RuntimeException.class);

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookCreateDto))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorDto.class);

        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
        verify(bookRepository).save(any());
    }

    @Test
    void saveNewBookBadRequestTest() {
        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new BookCreateDto()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void editBookPositiveTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));
        given(bookRepository.save(any())).willReturn(Mono.just(this.book));

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(this.book));

        verify(bookRepository).findById(anyString());
        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
        verify(bookRepository).save(any());
    }

    @Test
    void editBookBookNotFoundTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.empty());
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository).findById(anyString());
        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
    }

    @Test
    void editBookAuthorNotFoundTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(authorRepository.findById(anyString())).willReturn(Mono.empty());
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository).findById(anyString());
        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
    }

    @Test
    void editBookGenresNotFoundTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(Collections.emptyList()));

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository).findById(anyString());
        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
    }

    @Test
    void editBookError500Test() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(authorRepository.findById(anyString())).willReturn(Mono.just(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(Flux.fromIterable(this.book.getGenres()));
        given(bookRepository.save(any())).willThrow(RuntimeException.class);

        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookRepository).findById(anyString());
        verify(authorRepository).findById(anyString());
        verify(genreRepository).findAllById(anySet());
        verify(bookRepository).save(any());
    }

    @Test
    void editBookBadRequestTest() {
        webClient.put()
                .uri("/api/book/3")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new BookUpdateDto()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void findBookPositiveTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(commentRepository.findByBookId(any())).willReturn(Flux.just(new Comment("4", "comment text", this.book)));
        BookDto resultRef = bookMapper.toDto(this.book);
        resultRef.setComments(List.of(commentDto));

        webClient.get()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(resultRef);

        verify(bookRepository).findById("3");
        verify(commentRepository).findByBookId("3");
    }

    @Test
    void findBookError404Test() {
        given(bookRepository.findById(anyString())).willReturn(Mono.empty());
        given(commentRepository.findByBookId(any())).willReturn(Flux.just(new Comment("4", "comment text", this.book)));

        webClient.get()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().isNotFound();

        verify(bookRepository).findById("3");
    }

    @Test
    void findBookError500Test() {
        given(bookRepository.findById(anyString())).willThrow(RuntimeException.class);
        given(commentRepository.findByBookId(any())).willReturn(Flux.just(this.comment));

        webClient.get()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookRepository).findById("3");
    }

    @Test
    void deleteBookPositiveTest() {
        given(bookRepository.deleteById("3")).willReturn(Mono.empty());
        given(commentRepository.deleteByBookId("3")).willReturn(Mono.empty());

        webClient.delete()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().isOk();

        verify(bookRepository).deleteById("3");
        verify(commentRepository).deleteByBookId("3");

    }

    @Test
    void deleteBookError500Test() {
        given(bookRepository.deleteById("3")).willThrow(new RuntimeException());
        given(commentRepository.deleteByBookId("3")).willReturn(Mono.empty());

        webClient.delete()
                .uri("/api/book/3")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookRepository).deleteById("3");
        verify(commentRepository).deleteByBookId("3");
    }

     @Test
    void getCommentsForBookPositiveTest() {
        given(commentRepository.findByBookId(anyString())).willReturn(Flux.just(this.comment));

         webClient.get()
                 .uri("/api/book/3/comment")
                 .exchange()
                 .expectStatus().isOk()
                 .expectBodyList(CommentDto.class)
                 .hasSize(1)
                 .contains(this.commentDto);

        verify(commentRepository).findByBookId("3");
    }

   @Test
    void getCommentsForBookError500Test() {
       given(commentRepository.findByBookId(anyString())).willThrow(RuntimeException.class);

       webClient.get()
               .uri("/api/book/3/comment")
               .exchange()
               .expectStatus().is5xxServerError()
               .expectBody(ErrorDto.class);

       verify(commentRepository).findByBookId("3");
   }

    @Test
    void addNewCommentForBookPositiveTest() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(commentRepository.save(any())).willReturn(Mono.just(this.comment));
        CommentCreateDto request = new CommentCreateDto("comment text");

        webClient.post()
                .uri("/api/book/3/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(this.commentDto);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(bookRepository).findById("3");
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison()
                .isEqualTo(new Comment(null, "comment text", this.book));
    }

     @Test
    void addNewCommentForBookError404Test() {
         given(bookRepository.findById(anyString())).willReturn(Mono.empty());
         given(commentRepository.save(any())).willReturn(Mono.just(this.comment));
        CommentCreateDto request = new CommentCreateDto("comment text");

         webClient.post()
                 .uri("/api/book/3/comment")
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(BodyInserters.fromValue(request))
                 .exchange()
                 .expectStatus().isNotFound();

         verify(bookRepository).findById("3");
    }

    @Test
    void addNewCommentForBookError500Test() {
        given(bookRepository.findById(anyString())).willReturn(Mono.just(this.book));
        given(commentRepository.save(any())).willThrow(RuntimeException.class);
        CommentCreateDto request = new CommentCreateDto("comment text");

        webClient.post()
                .uri("/api/book/3/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().is5xxServerError();

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(bookRepository).findById("3");
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison()
                .isEqualTo(new Comment(null, "comment text", this.book));
    }

    @Test
    void addNewCommentForBookBadRequestTest() {
        webClient.post()
                .uri("/api/book/3/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new CommentCreateDto()))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
