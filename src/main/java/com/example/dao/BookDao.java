package com.example.dao;

import com.example.common.ObjectTranslator;
import com.example.controller.response.BookResponse;
import com.example.dao.repository.BookRepository;
import com.example.dto.BookDto;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BookDao {

    private final BookRepository bookRepository;
    private final ObjectTranslator translator;

    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .map(bookEntity -> translator.translate(bookEntity, BookDto.class));
    }

    public Flux<BookDto> searchByTitle(String title){
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .map(bookEntity -> translator.translate(bookEntity, BookDto.class));
    }

    public Flux<BookDto> searchByAuthor(String author) {
        return bookRepository.findByAuthorsContainingIgnoreCase(author)
                .map(bookEntity -> translator.translate(bookEntity, BookDto.class));
    }
}
