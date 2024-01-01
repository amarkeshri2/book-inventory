package com.example.dao.repository;

import com.example.dao.entity.BookEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends ReactiveMongoRepository<BookEntity, String> {
    Flux<BookEntity> findByTitleContainingIgnoreCase(String title);
    Flux<BookEntity> findByAuthorsContainingIgnoreCase(String author);
    Mono<BookEntity> findByBookId(Long id);

    Mono<Void> deleteByBookId(Long id);

    Flux<BookEntity> findByTitleIgnoreCaseContainingAndAuthorsIgnoreCaseContaining(String title, String author);
}
