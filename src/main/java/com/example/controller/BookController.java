package com.example.controller;

import com.example.controller.response.BookResponse;
import com.example.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {
    private final BookService bookService;


    @GetMapping
    public Flux<ResponseEntity<List<BookResponse>>> getAllBooks() {
//        return bookService.getAllBooks()
//                .map(ResponseEntity::ok)
//                .onErrorResume(ex -> {
//                    // Handle the error and return a specific ResponseEntity
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//                });
    }

    @PutMapping("/{id}/price")
    public Mono<ResponseEntity<BookResponse>> updateBookPrice(@PathVariable String id, @RequestParam Double newPrice) {

    }

    @PutMapping("/{id}/quantity")
    public Mono<BookResponse> updateBookQuantity(@PathVariable String id, @RequestParam int newQuantity) {

    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBook(@PathVariable String id) {

    }

    @GetMapping("/search/title")
    public Flux<BookResponse> searchByTitle(@RequestParam String title) {

    }

    @GetMapping("/search/author")
    public Flux<BookResponse> searchByAuthor(@RequestParam String author) {

    }
}