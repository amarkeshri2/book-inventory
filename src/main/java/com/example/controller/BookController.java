package com.example.controller;

import com.example.controller.book.Book;
import com.example.common.ObjectTranslator;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.AuditResponse;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;
import com.example.exceptions.AuditNotFound;
import com.example.exceptions.BookNotFoundException;
import com.example.common.BookEventPayload;
import com.example.producer.BookProducer;
import com.example.service.BookService;
import com.example.service.GoogleBooksAPIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.exceptions.BookAlreadyPresentException;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/v1/book")
@Validated
public class BookController {
    @Autowired
    private final BookService bookService;
    @Autowired
    private final ObjectTranslator translator;
    @Autowired
    private final GoogleBooksAPIService googleBooksAPIService;
    @Autowired
    private final BookProducer bookProducer;

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Mono<ResponseEntity<List<BookResponse>>> getAllBooks() {
        log.info("Received request to get all books");
        return bookService.getAllBooks().collectList()
                .map(books -> ResponseEntity.ok().body(books))
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred : {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing get all book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Mono<ResponseEntity<BookResponse>> getBookById(@PathVariable String id) {
        log.info("Received request to get book with ID: {}", id);
        return bookService.getBook(id)
                .map(book -> ResponseEntity.ok().body(book))
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing get book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }


    @GetMapping("/global-search")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<Book>>> globalSearchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author) {
        if (title == null && author == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        log.info("Received request for global search book");
        Flux<Book> books = googleBooksAPIService.searchBooks(title, author);
        return books.collectList()
                .map(book -> ResponseEntity.ok().body(book))
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing global search request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<BookResponse>> createBook(@Valid @RequestBody Book book) {
        log.info("Received request to create book : {}", book);
        BookDto bookDto = translator.translate(book, BookDto.class);
        return bookService.createBook(bookDto)
                .doOnSuccess(createdBook -> {
                    try {
                        BookEventPayload payload = translator.translate(createdBook, BookEventPayload.class);
                        payload.setTime(ZonedDateTime.now().toString());
                        payload.setEventType("CREATE");
                        log.info("Going to publish create event : {}", payload);
                        bookProducer.sendEvent(payload);
                    } catch (JsonProcessingException e) {
                        log.info("Error occurred during json parsing: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .map(bookdto1 -> ResponseEntity.status(HttpStatus.CREATED).body(bookdto1))
                .onErrorResume(err -> {
                    if (err instanceof BookAlreadyPresentException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.BAD_REQUEST)).build());
                    } else {
                        log.error("Error processing create book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<BookResponse>> updateBook(
            @PathVariable String id,
            @RequestBody @Valid final BookUpdateRequest updateRequest) {
        log.info("Received request to update book with ID : {}", id);
        if(updateRequest.getQuantity() ==null && updateRequest.getPrice() == null){
            log.info("both quantity and price is null");
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return bookService.updateBook(id, updateRequest)
                .doOnSuccess(updatedBook -> {
                    try {
                        BookEventPayload payload = translator.translate(updatedBook, BookEventPayload.class);
                        payload.setTime(ZonedDateTime.now().toString());
                        payload.setEventType("UPDATE");
                        log.info("Going to publish update event : {}", payload);
                        bookProducer.sendEvent(payload);
                    } catch (JsonProcessingException e) {
                        log.info("Error occurred during json parsing: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .map(bookResponse -> ResponseEntity.status(HttpStatus.ACCEPTED).body(bookResponse))
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing update book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });


    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable String id) {
        log.info("Received request to delete book with ID {} :", id);
        return bookService.deleteBook(id)
                .doOnSuccess(ex -> {
                    try {
                        BookEventPayload payload = new BookEventPayload();
                        payload.setBookId(id);
                        payload.setTime(ZonedDateTime.now().toString());
                        payload.setEventType("DELETE");
                        log.info("Going to publish delete event : {}", payload);
                        bookProducer.sendEvent(payload);
                    } catch (JsonProcessingException e) {
                        log.info("Error occurred during json parsing: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing delete book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Mono<ResponseEntity<List<BookResponse>>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author) {
        log.info("Received request to search book");
        if (title == null && author == null) {
            log.info("title and author are null");
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
                .onErrorResume(err -> {
                    if (err instanceof BookNotFoundException) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing search book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @GetMapping("/audit/{id}")
    @PreAuthorize(("hasRole('ADMIN')"))
    public Mono<ResponseEntity<List<AuditResponse>>> getAuditById(@PathVariable String id) {
        log.info("Received request to get audit history of bookId: {}", id);
        return bookService.getAudits(id).collectList()
                .map(audit -> ResponseEntity.ok().body(audit))
                .onErrorResume(err -> {
                    if (err instanceof AuditNotFound) {
                        log.error("Exception occurred {}", err.getMessage());
                        return Mono.just(ResponseEntity.status((HttpStatus.NOT_FOUND)).build());
                    } else {
                        log.error("Error processing get book request {}", err.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }
}