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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/book")
@AllArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;
    @Autowired
    private final ObjectTranslator translator;

    @GetMapping("/all")
    public ResponseEntity<Flux<BookResponse>> getAllBooks() {
        Flux<BookResponse> books = bookService.getAllBooks()
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found")));

        return ResponseEntity.ok().body(books);
    }

    @PutMapping()
    public ResponseEntity<Mono<BookResponse>> createBook(
            @Valid @RequestBody BookRequest bookRequest) {
        BookDto bookdto = translator.translate(bookRequest, BookDto.class);
        Mono<BookResponse> book = bookService.createBook(bookdto);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
    @GetMapping("/search")
    public ResponseEntity<Flux<BookResponse>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
                  @RequestParam(value = "author", required = false) String author) {
        if (title == null && author == null) {
            return ResponseEntity.badRequest().build();
        }

        Flux<BookResponse> books;
        if (title != null && author != null) {
            books = bookService.searchByTitleAndAuthor(title, author)
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found")));
        } else if (title != null) {
            books = bookService.searchByTitle(title)
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with this title")));
        } else {
            books = bookService.searchByAuthor(author)
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found by this author")));
        }

        return ResponseEntity.ok().body(books);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Mono<BookResponse>> updateBookPrice(
            @PathVariable String id,
            @RequestBody BookUpdateRequest updateRequest) {
        Mono<BookResponse> book = bookService.updateBook(id, updateRequest)
                .onErrorResume(BookNotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage())));

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(book);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}