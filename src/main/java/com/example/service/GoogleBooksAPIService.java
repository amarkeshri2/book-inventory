package com.example.service;

import com.example.controller.book.Book;
import com.example.exceptions.BookNotFoundException;
import com.example.googleBook.GoogleBookItem;
import com.example.googleBook.GoogleBooksResponse;
import com.example.googleBook.ImageLink;
import com.example.googleBook.VolumeInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
@AllArgsConstructor
public class GoogleBooksAPIService {

    @Autowired
    private final WebClient webClient;
    private static final String GOOGLE_BOOK_API_URI = "https://www.googleapis.com/books/v1/volumes";


    public Flux<Book> searchBooks(String title, String author) {
        String query = constructQuery(title, author);

        Mono<GoogleBooksResponse> googleBooksResponse = webClient.get()
                .uri(GOOGLE_BOOK_API_URI + "?q=" + query ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GoogleBooksResponse.class)
                ;

        return googleBooksResponse
                .flatMapMany(res -> Flux.fromIterable(res.getItems()))
                .map(this::mapToBook)
                .switchIfEmpty(Mono.error(new BookNotFoundException("No book found")));
    }

    private Book mapToBook(GoogleBookItem item) {
        VolumeInfo volumeInfo = item.getVolumeInfo();
        Object title = volumeInfo.getTitle();
        List<String> authors = volumeInfo.getAuthors();
        String description = volumeInfo.getDescription();
        ImageLink imageLinks = volumeInfo.getImageLinks();

        Book book = new Book();
        book.setBookId(item.getId());
        book.setTitle(title.toString());
        book.setAuthors(authors);
        book.setDescription(description);
        if(imageLinks != null)
            book.setImage(imageLinks.getImage());
        book.setPrice(1.0);
        book.setQuantity(1);
        return book;
    }

    private String constructQuery(String title, String author) {
        StringBuilder queryBuilder = new StringBuilder();
        if (title != null) {
            queryBuilder.append("intitle:").append(title);
        }
        if (author != null) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append("+");
            }
            queryBuilder.append("inauthor:").append(author);
        }
        return queryBuilder.toString();
    }


}
