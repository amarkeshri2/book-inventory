package com.example.service;

import com.example.controller.response.BookResponse;
import com.example.dao.BookDao;
import com.example.dao.repository.BookRepository;
import com.example.translator.BookTranslator;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
public class BookService {
    public Flux<List<BookResponse>> getAllBooks() {
    }

    private final BookDao bookDao;
    private final BookTranslator translator;


    public Mono<BookResponse> updateBookPrice(String id, Double newPrice) {
        return bookDao.findById(id)
                .flatMap(book -> {
                    book.setPrice(newPrice);
                    return bookDao.save(book);
                })
                .translator()
                .onErrorResume(NoSuchElementException.class, e -> Mono.empty());
    }
}
