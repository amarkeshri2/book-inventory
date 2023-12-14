package com.example.service;

import com.example.common.ObjectTranslator;
import com.example.controller.response.BookResponse;
import com.example.dao.BookDao;
import com.example.dto.BookDto;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;


import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class BookService {
    
    private final BookDao bookDao;
    private final ObjectTranslator translator;


    public Flux<BookResponse> getAllBooks() {
        return bookDao.getAllBooks()
                .map(bookDto -> translator.translate(bookDto, BookResponse.class));

    }

    public Flux<BookResponse> searchByTitle(String title ){
        return bookDao.searchByTitle(title)
                .map(bookDto -> translator.translate(bookDto,BookResponse.class));
    }

    public Flux<BookResponse> searchByAuthor(String author) {
        return bookDao.searchByAuthor(author)
                .map(bookDto -> translator.translate(bookDto,BookResponse.class));
    }
}
