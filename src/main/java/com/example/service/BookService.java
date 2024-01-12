package com.example.service;

import com.example.common.ObjectTranslator;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dao.BookDao;
import com.example.dto.BookDto;
import com.example.exceptions.BookAlreadyPresentException;
import com.example.exceptions.BookNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;


@Service
@AllArgsConstructor
public class BookService {

    private final BookDao bookDao;
    private final ObjectTranslator translator;


    public Flux<BookResponse> getAllBooks() {
        return bookDao.getAllBooks()
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Mono.error(new BookNotFoundException("No book found")));

    }

    public Mono<String> updateBook(String bookId, BookUpdateRequest updateRequest) {
        return bookDao.findByBookId(bookId)
                .flatMap(bookDto -> {
                    if (updateRequest.getPrice() != null) {
                        bookDto.setPrice(updateRequest.getPrice());
                    }
                    if (updateRequest.getQuantity() != null) {
                        bookDto.setQuantity(updateRequest.getQuantity());
                    }
                    bookDao.save(bookDto);
                    return Mono.just(bookDto.getBookId());
                })
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found for bookId: " + bookId)));

    }

    public Mono<BookResponse> getBook(String bookId) {
        return bookDao.findByBookId(bookId)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class))
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found for bookId: " + bookId)));
    }

    public Mono<BookResponse> createBook(BookDto bookdto) {
        return bookDao.findByBookId(bookdto.getBookId())
                .flatMap(existingBook -> {
                            if(existingBook != null)
                                return Mono.error(new BookAlreadyPresentException("Book already present for bookId: " + bookdto.getBookId()));
                            else {
                                return bookDao.save(bookdto)
                                        .map(dto -> translator.translate(dto, BookResponse.class));

                            }
                        }
                )
                .switchIfEmpty(bookDao.save(bookdto).map(dto -> translator.translate(dto, BookResponse.class)));
    }


    public Mono<Void> deleteBook(String id) {
        return bookDao.findByBookId(id)
                .flatMap(existing -> bookDao.deleteBook(existing.getBookId()))
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found for bookId: " + id)));

    }

    public Flux<BookResponse> searchByTitleAndAuthor(String title, String author) {
        return bookDao.searchByTitleAndAuthor(title, author)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class));
    }

    public Flux<BookResponse> searchByTitle(String title) {
        return bookDao.searchByTitle(title)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class));
    }

    public Flux<BookResponse> searchByAuthor(String author) {
        return bookDao.searchByAuthor(author)
                .map(bookDto -> translator.translate(bookDto, BookResponse.class));
    }


}
