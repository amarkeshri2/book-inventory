package com.example.controller;

import com.example.Utils.BookUtil;
import com.example.common.ObjectTranslator;
import com.example.controller.book.Book;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;
import com.example.exceptions.BookAlreadyPresentException;
import com.example.exceptions.BookNotFoundException;
import com.example.service.BookService;
import com.example.service.GoogleBooksAPIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookControllerTest {
    @InjectMocks
    private BookController bookController;
    @InjectMocks
    private ObjectMapper objectMapper;
    @Mock
    private BookService bookService;
    @Mock
    private GoogleBooksAPIService googleBooksAPIService;
    @Mock
    private ObjectTranslator objectTranslator;

    private MockMvc mockMvc;
    private static final String BASE_URI= "/v1/book";

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBooks() throws Exception {
        List<BookResponse> response = BookUtil.getBookResponseList();
        when(bookService.getAllBooks()).thenReturn(Flux.fromIterable(response));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    public void testCreateBook() throws Exception {
        BookResponse response = BookUtil.getBookResponse();
        Book request = BookUtil.getbook();
        BookDto bookDto = BookUtil.getBookDto();
        when(objectTranslator.translate(any(Book.class), Mockito.eq(BookDto.class))).thenReturn(bookDto);
        when(bookService.createBook(any(BookDto.class))).thenReturn(Mono.just(response));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post(BASE_URI )
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        verify(objectTranslator, times(1)).translate(request, BookDto.class);
        verify(bookService, times(1)).createBook(bookDto);
    }

    @Test
    void testUpdateBook() throws Exception {
        String bookId = "1L";
        BookUpdateRequest request = BookUtil.getBookUpdateRequest();
        when(bookService.updateBook(bookId, request)).thenReturn(Mono.just(bookId));

        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_URI + "/" + bookId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
        verify(bookService, times(1)).updateBook(bookId, request);
    }
    @Test
    void testDeleteBook() throws Exception {
        String bookId = "1L";
        when(bookService.deleteBook(bookId)).thenReturn(Mono.empty());
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(bookService, times(1)).deleteBook(bookId);
    }

    @Test
    void testGetBookById() throws Exception {
        String bookId = "1L";
        BookResponse response = BookUtil.getBookResponse();
        when(bookService.getBook(bookId)).thenReturn(Mono.just(response));
        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_URI +"/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(bookService, times(1)).getBook(bookId);
    }

    @Test
    void testGlobalSearchBooks() throws Exception {
        String title = "Sample Title";
        String author = "Sample Author";
        List<Book> response = BookUtil.getBookList();
        when(googleBooksAPIService.searchBooks(title, author)).thenReturn(Flux.fromIterable(response));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/global-search")
                                .param("title", title)
                                .param("author", author)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(googleBooksAPIService, times(1)).searchBooks(title, author);
    }

    @Test
    void testSearchBooks() throws Exception {
        String title = "Sample Title";
        String author = "Sample Author";
        List<BookResponse> response = BookUtil.getBookResponseList();
        when(bookService.searchByTitleAndAuthor(title, author)).thenReturn(Flux.fromIterable(response));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/search")
                                .param("title", title)
                                .param("author", author)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(bookService, times(1)).searchByTitleAndAuthor(title, author);
    }

    @Test
    void testCreateBookWithExistingBookException() throws Exception {
        Book request = BookUtil.getbook();
        BookDto bookDto = BookUtil.getBookDto();
        when(objectTranslator.translate(any(Book.class), Mockito.eq(BookDto.class))).thenReturn(bookDto);
        when(bookService.createBook(any(BookDto.class))).thenThrow(new BookAlreadyPresentException("Book Already present"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URI)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        verify(objectTranslator, times(1)).translate(request, BookDto.class);
        verify(bookService, times(1)).createBook(bookDto);
    }

    @Test
    void testUpdateNonexistentBookException() throws Exception {
        String bookId = "1L";
        BookUpdateRequest request = BookUtil.getBookUpdateRequest();
        when(bookService.updateBook(bookId, any(BookUpdateRequest.class))).thenThrow(new BookNotFoundException("No Book found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_URI + "/" + bookId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        verify(bookService, times(1)).updateBook(bookId, request);
    }

    @Test
    void testDeleteNonexistentBookException() throws Exception {
        String bookId = "1L";
        when(bookService.deleteBook(bookId)).thenThrow(new BookNotFoundException("No Book found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_URI + "/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        verify(bookService, times(1)).deleteBook(bookId);
    }

    @Test
    void testGetNonexistentBookByIdException() throws Exception {
        String bookId = "1L";
        when(bookService.getBook(bookId)).thenThrow(new BookNotFoundException("No Book found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(bookService, times(1)).getBook(bookId);
    }

    @Test
    void testGlobalSearchBooksException() throws Exception {
        String title = "Sample Title";
        String author = "Sample Author";
        when(googleBooksAPIService.searchBooks(title, author)).thenThrow(new BookNotFoundException("No Book found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/global-search")
                                .param("title", title)
                                .param("author", author)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(googleBooksAPIService, times(1)).searchBooks(title, author);
    }

    @Test
    void testSearchBooksException() throws Exception {
        String title = "Sample Title";
        String author = "Sample Author";
        when(bookService.searchByTitleAndAuthor(title, author)).thenThrow(new BookNotFoundException("No Book found"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URI + "/search")
                                .param("title", title)
                                .param("author", author)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(bookService, times(1)).searchByTitleAndAuthor(title, author);
    }
}
