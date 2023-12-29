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
import com.example.exceptions.BookAlreadyPresentException;
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
    public Flux<ResponseEntity<BookResponse>> getAllBooks() {
        Flux<BookResponse> books = bookService.getAllBooks()
                .switchIfEmpty(
                        Flux.error(
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found")));

        return books.map(book -> ResponseEntity.ok().body(book));
    }


    @PutMapping()
    public Mono<ResponseEntity<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        try {
            BookDto bookDto = translator.translate(bookRequest, BookDto.class);
            Mono<BookResponse> createdBook = bookService.createBook(bookDto);
            return createdBook.map(book ->
                    ResponseEntity.status(HttpStatus.CREATED).body(book));
        } catch (BookAlreadyPresentException ex) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
        } catch (Exception ex) {
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<BookResponse>> updateBookPrice(
            @PathVariable String id,
            @RequestBody BookUpdateRequest updateRequest) {
        try {
            Mono<BookResponse> updateBook = bookService.updateBook(id, updateRequest);
            return updateBook.map(bookResponse ->
                    ResponseEntity.status(HttpStatus.CREATED).body(bookResponse));
        }
        catch(BookNotFoundException ex){
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
        }
        catch(Exception ex){
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable String id) {
        try{
            bookService.deleteBook(id);
            return Mono.just(ResponseEntity.ok().build());
        }
        catch(BookNotFoundException ex){
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
        }
        catch(Exception ex){
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
        }
    }

    @GetMapping("/search")
    public Flux<ResponseEntity<BookResponse>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author) {
        if (title == null && author == null) {
            return Flux.just(ResponseEntity.badRequest().build());
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

        return books.map(book ->
                ResponseEntity.ok().body(book));
    }




}