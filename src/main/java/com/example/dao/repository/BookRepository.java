package com.example.dao.repository;

import com.example.dao.entity.BookEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface BookRepository extends ReactiveMongoRepository<BookEntity, String> {
    Flux<BookEntity> findByTitleContainingIgnoreCase(String title);
    Flux<BookEntity> findByAuthorsContainingIgnoreCase(String author);
    Mono<BookEntity> findByBookId(String bookId);

    Mono<Void> deleteByBookId(String bookId);

    Flux<BookEntity> findByTitleIgnoreCaseContainingAndAuthorsIgnoreCaseContaining(String title, String author);
}
