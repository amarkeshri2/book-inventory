package com.example.dao.repository;

import com.example.dao.entity.AuditEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface AuditRepository extends ReactiveMongoRepository<AuditEntity, String> {
     Flux<AuditEntity> findByBookId(String bookId);
}
