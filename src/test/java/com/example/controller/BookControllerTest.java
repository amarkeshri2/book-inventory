package com.example.controller;

import com.example.Utils.BookUtil;
import com.example.common.BookEventPayload;
import com.example.common.ObjectTranslator;
import com.example.config.TestSecurityConfig;
import com.example.controller.book.Book;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;
import com.example.exceptions.BookAlreadyPresentException;
import com.example.exceptions.BookNotFoundException;
import com.example.producer.BookProducer;
import com.example.service.BookService;
import com.example.service.GoogleBooksAPIService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(BookController.class)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
public class BookControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BookService bookService;
    @MockBean
    private GoogleBooksAPIService googleBooksAPIService;
    @MockBean
    private ObjectTranslator objectTranslator;
    @MockBean
    private BookProducer producer;
    @InjectMocks
    private BookController bookController;
    private static final String BASE_URI = "/v1/book";

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    public void testGetAllBooks() throws Exception {
        List<BookResponse> response = BookUtil.getBookResponseList();
        when(bookService.getAllBooks()).thenReturn(Flux.fromIterable(response));

        webTestClient
                .get().uri(BASE_URI + "/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateBook() throws Exception {
        BookResponse response = BookUtil.getBookResponse();
        Book request = BookUtil.getbook();
        BookDto bookDto = BookUtil.getBookDto();
        BookEventPayload payload = BookUtil.getEventPayload();
        when(objectTranslator.translate(any(Book.class), Mockito.eq(BookDto.class))).thenReturn(bookDto);
        when(bookService.createBook(any(BookDto.class))).thenReturn(Mono.just(response));
        when(objectTranslator.translate(any(BookResponse.class), Mockito.eq(BookEventPayload.class))).thenReturn(payload);
        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookResponse.class).isEqualTo(response);

        verify(objectTranslator, times(1)).translate(request, BookDto.class);
        verify(bookService, times(1)).createBook(bookDto);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBook() {
        String bookId = "1L";
        BookUpdateRequest request = BookUtil.getBookUpdateRequest();
        BookEventPayload payload = BookUtil.getEventPayload();
        BookResponse response = BookUtil.getBookResponse();
        when(bookService.updateBook(bookId, request)).thenReturn(Mono.just(response));
        when(objectTranslator.translate(any(BookResponse.class), Mockito.eq(BookEventPayload.class))).thenReturn(payload);
        webTestClient.patch().uri(BASE_URI + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue( request)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BookResponse.class).isEqualTo(response);

        verify(bookService, times(1)).updateBook(bookId, request);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBook() {
        String bookId = "1L";
        when(bookService.deleteBook(bookId)).thenReturn(Mono.empty());

        webTestClient.delete().uri(BASE_URI + "/" + bookId)
                .exchange()
                .expectStatus().isOk();

        verify(bookService, times(1)).deleteBook(bookId);
    }


    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetBookById() {
        String bookId = "1L";
        BookResponse response = BookUtil.getBookResponse();
        when(bookService.getBook(bookId)).thenReturn(Mono.just(response));

        webTestClient.get().uri(BASE_URI + "/" + bookId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class).isEqualTo(response);

        verify(bookService, times(1)).getBook(bookId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGlobalSearchBooks() {
        String title = "Sample Title";
        String author = "Sample Author";
        List<Book> response = BookUtil.getBookList();
        when(googleBooksAPIService.searchBooks(title, author)).thenReturn(Flux.fromIterable(response));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(BASE_URI + "/global-search")
                        .queryParam("title", title)
                        .queryParam("author", author)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class).isEqualTo(response);

        verify(googleBooksAPIService, times(1)).searchBooks(title, author);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchBooks() {
        String title = "Sample Title";
        String author = "Sample Author";
        List<BookResponse> response = BookUtil.getBookResponseList();
        when(bookService.searchByTitleAndAuthor(title, author)).thenReturn(Flux.fromIterable(response));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(BASE_URI + "/search")
                        .queryParam("title", title)
                        .queryParam("author", author)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookResponse.class).isEqualTo(response);

        verify(bookService, times(1)).searchByTitleAndAuthor(title, author);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateBookWithExistingBookException() {
        Book request = BookUtil.getbook();
        BookDto bookDto = BookUtil.getBookDto();
        when(objectTranslator.translate(any(Book.class), Mockito.eq(BookDto.class))).thenReturn(bookDto);
        when(bookService.createBook(bookDto)).thenReturn(Mono.error(new BookAlreadyPresentException("Book Already present")));

        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        verify(objectTranslator, times(1)).translate(request, BookDto.class);
        verify(bookService, times(1)).createBook(bookDto);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateNonexistentBookException() {
        String bookId = "1L";
        BookUpdateRequest request = BookUtil.getBookUpdateRequest();
        when(bookService.updateBook(bookId, request)).thenReturn(Mono.error(new BookNotFoundException("Book not found")));

        webTestClient.patch().uri(BASE_URI + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookService, times(1)).updateBook(bookId, request);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteNonexistentBookException() {
        String bookId = "1L";
        when(bookService.deleteBook(bookId)).thenReturn(Mono.error(new BookNotFoundException("Book not found")));

        webTestClient.delete().uri(BASE_URI + "/" + bookId)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookService, times(1)).deleteBook(bookId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetNonexistentBookByIdException() {
        String bookId = "1L";
        when(bookService.getBook(bookId)).thenReturn(Mono.error(new BookNotFoundException("Book not found")));

        webTestClient.get().uri(BASE_URI + "/" + bookId)
                .exchange()
                .expectStatus().isNotFound();

        verify(bookService, times(1)).getBook(bookId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGlobalSearchBooksException() {
        String title = "Sample Title";
        String author = "Sample Author";
        when(googleBooksAPIService.searchBooks(title, author)).thenReturn(Flux.error(new BookNotFoundException("Book not found")));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(BASE_URI + "/global-search")
                        .queryParam("title", title)
                        .queryParam("author", author)
                        .build())
                .exchange()
                .expectStatus().isNotFound();

        verify(googleBooksAPIService, times(1)).searchBooks(title, author);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchBooksException() {
        String title = "Sample Title";
        String author = "Sample Author";
        when(bookService.searchByTitleAndAuthor(title, author)).thenReturn(Flux.error(new BookNotFoundException("Book not found")));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(BASE_URI + "/search")
                        .queryParam("title", title)
                        .queryParam("author", author)
                        .build())
                .exchange()
                .expectStatus().isNotFound();

        verify(bookService, times(1)).searchByTitleAndAuthor(title, author);
    }

}
