package com.example.dao;

import com.example.dto.BookDto;
import reactor.core.publisher.Mono;

public class BookDao {
    public Mono<BookDto> findById(String id) {
        return null;
    }

    public Mono<BookDto> save(BookDto book) {
    }
}
