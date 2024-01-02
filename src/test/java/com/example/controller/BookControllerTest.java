package com.example.controller;

import com.example.Utils.BookUtil;
import com.example.common.ObjectTranslator;
import com.example.controller.request.BookRequest;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;
import com.example.service.BookService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookControllerTest {
    @InjectMocks
    private BookController bookController;
    @InjectMocks
    private ObjectMapper objectMapper;
    @Mock
    private BookService bookService;
    @Mock
    private ObjectTranslator objectTranslator;

    private MockMvc mockMvc;
    private static String URI_WITH_PARAM = "/api/book";

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBooks() throws Exception {
        List<BookResponse> response = BookUtil.getBookList();
        when(bookService.getAllBooks()).thenReturn(Flux.fromIterable(response));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get(URI_WITH_PARAM + "/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateBook() throws Exception {
        BookResponse response = BookUtil.getBookResponse();
        BookRequest request = BookUtil.getbookRequest();
        BookDto bookDto = BookUtil.getbookDto();
        when(objectTranslator.translate(any(BookRequest.class), Mockito.eq(BookDto.class))).thenReturn(bookDto);
        when(bookService.createBook(any(BookDto.class))).thenAnswer(invocation -> Mono.just(response));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post(URI_WITH_PARAM )
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }




}
