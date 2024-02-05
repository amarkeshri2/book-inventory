package com.example.service;

import com.example.Utils.BookUtil;
import com.example.controller.book.Book;
import com.example.googleBook.GoogleBooksResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

public class GoogleBooksAPIServiceIntegrationTest {

    private static MockWebServer mockWebServer;
    @InjectMocks
    private static GoogleBooksAPIService googleBooksAPIService;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        googleBooksAPIService = new GoogleBooksAPIService(webClient);
        objectMapper = new ObjectMapper();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testSearchBooks() throws JsonProcessingException {
        GoogleBooksResponse response = BookUtil.getGoogleBooksResponse();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));


        Flux<Book> result = googleBooksAPIService.searchBooks("Mock Book", "Mock Author");

        StepVerifier.create(result)
                .expectNextMatches(book -> book.getTitle().equals("Mock Book") &&
                        book.getAuthors().contains("Mock Author") &&
                        book.getDescription().equals("Mock Description"))
                .verifyComplete();
    }
}
