package com.example.dao.repository;

import com.example.dao.entity.BookEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;


public interface BookRepository extends ReactiveMongoRepository<BookEntity, String> {
    Flux<List<BookEntity>> findByTitleContainingIgnoreCase(String title);
    Flux<List<BookEntity>> findByAuthorsContainingIgnoreCase(String author);
}
