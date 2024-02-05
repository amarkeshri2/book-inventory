package com.example.Utils;

import com.example.common.BookEventPayload;
import com.example.controller.book.Book;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dao.entity.BookEntity;
import com.example.dto.BookDto;
import com.example.googleBook.GoogleBookItem;
import com.example.googleBook.GoogleBooksResponse;
import com.example.googleBook.ImageLink;
import com.example.googleBook.VolumeInfo;

import java.time.ZonedDateTime;
import java.util.List;

public class BookUtil {
    public static List<BookResponse> getBookResponseList(){
        BookResponse response1 = new BookResponse();
        response1.setBookId("1L");
        response1.setPrice(1.1);
        response1.setQuantity(1);
        response1.setImage("abc");
        response1.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response1.setTitle("abc");
        response1.setDescription("book");
        BookResponse response2 = new BookResponse();
        response2.setBookId("2L");
        response2.setPrice(1.1);
        response2.setQuantity(1);
        response2.setImage("abc");
        response2.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response2.setTitle("abc");
        response2.setDescription("book");
        return List.of(response1,response2);
    }

    public static BookResponse getBookResponse() {
        BookResponse response1 = new BookResponse();
        response1.setBookId("1L");
        response1.setPrice(1.1);
        response1.setQuantity(1);
        response1.setImage("abc");
        response1.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response1.setTitle("abc");
        response1.setDescription("book");
        return response1;
    }

    public static Book getbook() {
        Book book = new Book();
        book.setBookId("1L");
        book.setPrice(1.1);
        book.setQuantity(1);
        book.setImage("abc");
        book.setAuthors(List.of(new String[]{"abc", "xyz"}));
        book.setTitle("abc");
        book.setDescription("book");
        return book;
    }

    public static BookDto getBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setBookId("1L");
        bookDto.setPrice(1.1);
        bookDto.setQuantity(1);
        bookDto.setImage("abc");
        bookDto.setAuthors(List.of(new String[]{"abc", "xyz"}));
        bookDto.setTitle("abc");
        bookDto.setDescription("book");
        return bookDto;
    }

    public static BookUpdateRequest getBookUpdateRequest() {
        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setPrice(2.1);
        updateRequest.setQuantity(2);
        return updateRequest;
    }

    public static List<Book> getBookList() {
        Book response1 = new Book();
        response1.setBookId("1L");
        response1.setPrice(1.1);
        response1.setQuantity(1);
        response1.setImage("abc");
        response1.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response1.setTitle("abc");
        response1.setDescription("book");
        Book response2 = new Book();
        response2.setBookId("2L");
        response2.setPrice(1.1);
        response2.setQuantity(1);
        response2.setImage("abc");
        response2.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response2.setTitle("abc");
        response2.setDescription("book");
        return List.of(response1,response2);
    }


    public static GoogleBooksResponse getGoogleBooksResponse() {
        GoogleBooksResponse response = new GoogleBooksResponse();
        GoogleBookItem bookItem = new GoogleBookItem();
        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo.setTitle("Mock Book");
        volumeInfo.setAuthors(List.of("Mock Author"));
        volumeInfo.setDescription("Mock Description");
        ImageLink imageLinks = new ImageLink();
        imageLinks.setImage("Mock Image URL");
        volumeInfo.setImageLinks(imageLinks);
        bookItem.setId("MockBookId");
        bookItem.setVolumeInfo(volumeInfo);
        response.setKind("volume");
        response.setTotalItems(1);
        response.setItems(List.of(bookItem));
        return response;
    }

    public static BookEntity getBookEntity() {
        BookEntity book = new BookEntity();
        book.setBookId("1L");
        book.setPrice(1.1);
        book.setQuantity(1);
        book.setImage("abc");
        book.setAuthors(List.of(new String[]{"abc", "xyz"}));
        book.setTitle("abc");
        book.setDescription("book");
        book.setId("id");
        return book;
    }

    public static List<BookDto> getBookDtoList() {
        BookDto response1 = new BookDto();
        response1.setBookId("1L");
        response1.setPrice(1.1);
        response1.setQuantity(1);
        response1.setImage("abc");
        response1.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response1.setTitle("abc");
        response1.setDescription("book");
        BookDto response2 = new BookDto();
        response2.setBookId("2L");
        response2.setPrice(1.1);
        response2.setQuantity(1);
        response2.setImage("abc");
        response2.setAuthors(List.of(new String[]{"abc", "xyz"}));
        response2.setTitle("abc");
        response2.setDescription("book");
        return List.of(response1,response2);
    }

    public static BookEventPayload getEventPayload() {
        BookEventPayload book = new BookEventPayload();
        book.setBookId("1L");
        book.setPrice(1.1);
        book.setQuantity(1);
        book.setImage("abc");
        book.setAuthors(List.of(new String[]{"abc", "xyz"}));
        book.setTitle("abc");
        book.setDescription("book");
        book.setTime(ZonedDateTime.now().toString());
        book.setEventType("CREATE");
        return book;
    }
}
