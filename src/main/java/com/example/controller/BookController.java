package com.example.controller;

import com.example.common.ObjectTranslator;
import com.example.controller.request.BookRequest;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;
import com.example.exceptions.BookNotFoundException;
import com.example.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.exceptions.BookAlreadyPresentException;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/book")
@AllArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;
    @Autowired
    private final ObjectTranslator translator;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Mono<ResponseEntity<List<BookResponse>>> getAllBooks() {
        return bookService.getAllBooks().collectList()
                .map(books -> ResponseEntity.ok().body(books))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<BookResponse>> createBook(
            @Valid @RequestBody BookRequest bookRequest) {
        BookDto bookDto = translator.translate(bookRequest, BookDto.class);
        return bookService.createBook(bookDto)
                .map(book -> ResponseEntity.status(HttpStatus.CREATED).body(book))
                .onErrorResume(
                        BookAlreadyPresentException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())
                )
                .onErrorResume(
                        throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                );
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<BookResponse>> updateBook(
            @PathVariable String id,
           @Valid @RequestBody BookUpdateRequest updateRequest) {

        return bookService.updateBook(id, updateRequest)
                .map(book -> ResponseEntity.status(HttpStatus.ACCEPTED).body(book))
                .onErrorResume(
                        BookNotFoundException.class,
                        ex -> Mono.just(ResponseEntity.status((HttpStatus.BAD_REQUEST)).build())
                )
                .onErrorResume(
                        throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                );


    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable String id) {
        return bookService.deleteBook(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(BookNotFoundException.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).<Void>build()))
                .onErrorResume(Exception.class,
                        ex -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build()));
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<BookResponse>>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author) {
        if (title == null && author == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        Flux<BookResponse> books;
        if (title != null && author != null) {
            books = bookService.searchByTitleAndAuthor(title, author);
        } else if (title != null) {
            books = bookService.searchByTitle(title);
        } else {
            books = bookService.searchByAuthor(author);
        }

        return books.collectList()
                .map(book -> ResponseEntity.ok().body(book))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found")));
    }
}