package com.example.service;

import com.example.Utils.BookUtil;
import com.example.common.ObjectTranslator;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.AuditResponse;
import com.example.controller.response.BookResponse;
import com.example.dao.AuditDao;
import com.example.dao.BookDao;
import com.example.dto.AuditDto;
import com.example.dto.BookDto;
import com.example.exceptions.AuditNotFound;
import com.example.exceptions.BookAlreadyPresentException;
import com.example.exceptions.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class BookServiceTest {
    @InjectMocks
    private BookService bookService;
    @Mock
    private BookDao bookDao;
    @Mock
    private ObjectTranslator objectTranslator;

    @Mock
    private AuditDao auditDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBook() {
        List<BookDto> mockBookDto = BookUtil.getBookDtoList();
        List<BookResponse> mockBookResponseList = BookUtil.getBookResponseList();
        when(bookDao.getAllBooks()).thenReturn(Flux.fromIterable(mockBookDto));
        when(objectTranslator.translate(any(BookDto.class), any())).thenReturn(mockBookResponseList.get(0), mockBookResponseList.get(1));

        Flux<BookResponse> result = bookService.getAllBooks();

        StepVerifier.create(result)
                .expectNext(mockBookResponseList.get(0))
                .expectNext(mockBookResponseList.get(1))
                .expectComplete()
                .verify();
    }

    @Test
    public void testGetAllBooksNoBooksFound() {
        when(bookDao.getAllBooks()).thenReturn(Flux.empty());

        Flux<BookResponse> result = bookService.getAllBooks();

        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    public void testUpdateBook() {
        String bookId = "1L";
        BookUpdateRequest updateRequest = BookUtil.getBookUpdateRequest();
        BookDto mockBookDto = BookUtil.getBookDto();
        BookResponse response = BookUtil.getBookResponse();
        when(bookDao.findByBookId(bookId)).thenReturn(Mono.just(mockBookDto));
        when(bookDao.save(mockBookDto)).thenReturn(Mono.just(mockBookDto));
        when(objectTranslator.translate(any(BookDto.class), Mockito.eq(BookResponse.class))).thenReturn(response);
        Mono<BookResponse> result = bookService.updateBook(bookId, updateRequest);

        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    public void testUpdateBookNotFound() {
        String bookId = "1L";
        BookUpdateRequest updateRequest = BookUtil.getBookUpdateRequest();

        when(bookDao.findByBookId(bookId)).thenReturn(Mono.empty());

        Mono<BookResponse> result = bookService.updateBook(bookId, updateRequest);

        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    public void testGetBook() {
        String bookId = "3";
        BookDto mockBookDto = BookUtil.getBookDto();
        when(bookDao.findByBookId(bookId)).thenReturn(Mono.just(mockBookDto));

        BookResponse mockBookResponse = BookUtil.getBookResponse();
        when(objectTranslator.translate(mockBookDto, BookResponse.class)).thenReturn(mockBookResponse);

        Mono<BookResponse> result = bookService.getBook(bookId);

        StepVerifier.create(result)
                .expectNext(mockBookResponse)
                .verifyComplete();
    }

    @Test
    public void testGetBookNotFound() {
        String bookId = "4";
        when(bookDao.findByBookId(bookId)).thenReturn(Mono.empty());

        Mono<BookResponse> result = bookService.getBook(bookId);

        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    public void testCreateBook() {
        BookDto bookDto = BookUtil.getBookDto();

        when(bookDao.findByBookId(bookDto.getBookId())).thenReturn(Mono.empty());
        when(bookDao.save(bookDto)).thenReturn(Mono.just(bookDto));

        BookResponse mockBookResponse = BookUtil.getBookResponse();
        when(objectTranslator.translate(bookDto, BookResponse.class)).thenReturn(mockBookResponse);

        Mono<BookResponse> result = bookService.createBook(bookDto);

        StepVerifier.create(result)
                .expectNext(mockBookResponse)
                .verifyComplete();
    }

    @Test
    public void testCreateBookAlreadyPresent() {
        BookDto bookDto = BookUtil.getBookDto();

        when(bookDao.findByBookId(bookDto.getBookId())).thenReturn(Mono.just(bookDto));
        when(bookDao.save(any(BookDto.class))).thenReturn(Mono.empty());

        Mono<BookResponse> result = bookService.createBook(bookDto);

        StepVerifier.create(result)
                .expectError(BookAlreadyPresentException.class)
                .verify();
    }

    @Test
    public void testDeleteBook() {
        String bookId = "1L";
        BookDto mockBookDto = BookUtil.getBookDto();

        when(bookDao.findByBookId(any(String.class))).thenReturn(Mono.just(mockBookDto));
        when(bookDao.deleteBook(any(String.class))).thenReturn(Mono.empty());

        Mono<Void> result = bookService.deleteBook(bookId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void testDeleteBookNotFound() {
        String bookId = "8";
        when(bookDao.findByBookId(bookId)).thenReturn(Mono.empty());

        Mono<Void> result = bookService.deleteBook(bookId);

        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    public void testSearchByTitleAndAuthor() {
        String title = "TestBook";
        String author = "TestAuthor";
        BookDto mockBookDto = BookUtil.getBookDto();
        Flux<BookDto> mockBookDtoFlux = Flux.just(mockBookDto);

        when(bookDao.searchByTitleAndAuthor(title, author)).thenReturn(mockBookDtoFlux);

        BookResponse mockBookResponse = BookUtil.getBookResponse();
        when(objectTranslator.translate(mockBookDto, BookResponse.class)).thenReturn(mockBookResponse);

        Flux<BookResponse> result = bookService.searchByTitleAndAuthor(title, author);

        StepVerifier.create(result)
                .expectNext(mockBookResponse)
                .verifyComplete();
    }

    @Test
    public void testSearchByTitle() {
        String title = "TestBook";
        BookDto mockBookDto = BookUtil.getBookDto();
        Flux<BookDto> mockBookDtoFlux = Flux.just(mockBookDto);

        when(bookDao.searchByTitle(title)).thenReturn(mockBookDtoFlux);

        BookResponse mockBookResponse = BookUtil.getBookResponse();
        when(objectTranslator.translate(mockBookDto, BookResponse.class)).thenReturn(mockBookResponse);

        Flux<BookResponse> result = bookService.searchByTitle(title);

        StepVerifier.create(result)
                .expectNext(mockBookResponse)
                .verifyComplete();
    }

    @Test
    public void testSearchByAuthor() {
        String author = "TestAuthor";
        BookDto mockBookDto = BookUtil.getBookDto();
        Flux<BookDto> mockBookDtoFlux = Flux.just(mockBookDto);

        when(bookDao.searchByAuthor(author)).thenReturn(mockBookDtoFlux);

        BookResponse mockBookResponse = BookUtil.getBookResponse();
        when(objectTranslator.translate(mockBookDto, BookResponse.class)).thenReturn(mockBookResponse);

        Flux<BookResponse> result = bookService.searchByAuthor(author);

        StepVerifier.create(result)
                .expectNext(mockBookResponse)
                .verifyComplete();
    }

    @Test
    void testGetAuditsByBookId(){
        List<AuditDto> auditDtos = BookUtil.getAuditDtoList();
        List<AuditResponse> responses = BookUtil.getAuditResponseList();

        String bookId= "1L";

        when(auditDao.getAudits(bookId)).thenReturn(Flux.fromIterable(auditDtos));
        when(objectTranslator.translate(any(AuditDto.class), Mockito.eq(AuditResponse.class))).thenReturn(responses.get(0), responses.get(1));

        Flux<AuditResponse> result = bookService.getAudits(bookId);
        StepVerifier.create(result)
                .expectNext(responses.get(0))
                .expectNext(responses.get(1))
                .verifyComplete();
    }


    @Test
    void testGetAuditsNoAuditFound() {
        String bookId = "1L";
        when(auditDao.getAudits(bookId)).thenReturn(Flux.empty());

        Flux<AuditResponse> result = bookService.getAudits(bookId);
        StepVerifier.create(result)
                .expectError(AuditNotFound.class)
                .verify();
    }
}




