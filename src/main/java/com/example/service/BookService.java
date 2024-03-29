package com.example.service;

import com.example.common.ObjectTranslator;

import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.AuditResponse;
import com.example.controller.response.BookResponse;
import com.example.dao.AuditDao;
import com.example.dao.BookDao;
import com.example.dto.BookDto;
import com.example.exceptions.AuditNotFound;
import com.example.exceptions.BookAlreadyPresentException;
import com.example.exceptions.BookNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@AllArgsConstructor
public class BookService {

    private final BookDao bookDao;
    private final ObjectTranslator translator;
    private final AuditDao auditDao;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    public Flux<BookResponse> getAllBooks() {
        return bookDao.getAllBooks()
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Flux.error(new BookNotFoundException("No book found")));

    }

    public Mono<BookResponse> updateBook(String bookId, BookUpdateRequest updateRequest) {
        Mono<BookDto> bookDtoMono = bookDao.findByBookId(bookId);
        return bookDtoMono.flatMap(bookDto -> {
                    if (updateRequest.getPrice() != null) {
                        bookDto.setPrice(updateRequest.getPrice());
                    }
                    if (updateRequest.getQuantity() != null) {
                        bookDto.setQuantity(updateRequest.getQuantity());
                    }
                    return bookDao.save(bookDto)
                            .map(dto -> translator.translate(dto, BookResponse.class));
                })
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found with bookId: " + bookId)));

    }

    public Mono<BookResponse> getBook(String bookId) {
        return bookDao.findByBookId(bookId)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found with bookId: " + bookId)));
    }

    public Mono<BookResponse> createBook(BookDto bookdto) {
        return bookDao.findByBookId(bookdto.getBookId())
                .flatMap(existingBook -> {
                    if (existingBook != null)
                        return Mono.error(new BookAlreadyPresentException("Book already present with bookId: " + bookdto.getBookId()));
                    else {
                        return bookDao.save(bookdto)
                                .map(dto -> translator.translate(dto, BookResponse.class));
                    }
                })
                .switchIfEmpty(bookDao.save(bookdto)
                        .map(dto -> translator.translate(dto, BookResponse.class)));
    }

    public Mono<Void> deleteBook(String id) {
        return bookDao.findByBookId(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found with bookId: " + id)))
                .flatMap(existing -> bookDao.deleteBook(existing.getBookId()))
                .then();
    }

    public Flux<BookResponse> searchByTitleAndAuthor(String title, String author) {
        return bookDao.searchByTitleAndAuthor(title, author)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Mono.error(new BookNotFoundException("No Book found")));
    }

    public Flux<BookResponse> searchByTitle(String title) {
        return bookDao.searchByTitle(title)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Flux.error(new BookNotFoundException("No Book found")));
    }

    public Flux<BookResponse> searchByAuthor(String author) {
        return bookDao.searchByAuthor(author)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Flux.error(new BookNotFoundException("No Book found")));
    }
    public Flux<AuditResponse> getAudits(String id){
        return auditDao.getAudits(id)
                .map(auditDto -> translator.translate(auditDto, AuditResponse.class))
                .switchIfEmpty(Flux.error(new AuditNotFound("No audit history found for book with bookId: "+ id)));
    }


}
