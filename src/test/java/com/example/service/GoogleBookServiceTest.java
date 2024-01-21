package com.example.service;

import com.example.Utils.BookUtil;
import com.example.controller.book.Book;
import com.example.exceptions.BookNotFoundException;
import com.example.googleBook.GoogleBooksResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GoogleBookServiceTest {
    @InjectMocks
    private GoogleBooksAPIService googleBooksAPIService;

    @Mock
    private WebClient webClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchBooks() {
        String title = "Spring";
        String author = "Author";

        GoogleBooksResponse response = BookUtil.getGoogleBooksResponse();


        when(webClient.get().uri(any(String.class)).accept(any(MediaType.class)).retrieve().bodyToMono(GoogleBooksResponse.class))
                .thenReturn(Mono.just(response));

        Flux<Book> result = googleBooksAPIService.searchBooks(title, author);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testSearchBooksNoBooksFound() {
        String title = "NonExistingTitle";
        String author = "NonExistingAuthor";

        when(webClient.get().uri(any(String.class)).accept(any(MediaType.class)).retrieve().bodyToMono(GoogleBooksResponse.class))
                .thenReturn(Mono.empty());

        Flux<Book> result = googleBooksAPIService.searchBooks(title, author);

        StepVerifier.create(result)
                .expectError(BookNotFoundException.class)
                .verify();
    }


}
