package com.example.dao;

import com.example.common.ObjectTranslator;
import com.example.dao.entity.BookEntity;
import com.example.dao.repository.BookRepository;
import com.example.dto.BookDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
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

    public Mono<BookDto> findByBookId(Long id) {
        return bookRepository.findByBookId(id)
                .map(bookEntity -> translator.translate(bookEntity, BookDto.class));

    }

    public Mono<BookDto> save(BookDto bookDto) {
        BookEntity bookEntity = translator.translate(bookDto, BookEntity.class);
        return bookRepository.save(bookEntity)
                .map(book -> translator.translate(book, BookDto.class));
    }

    public Mono<Void> deleteBook(Long id) {
       return bookRepository.deleteByBookId(id);
    }

    public Flux<BookDto> searchByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleIgnoreCaseContainingAndAuthorsIgnoreCaseContaining(title, author)
                .map(bookEntity -> translator.translate(bookEntity, BookDto.class));
    }
}
