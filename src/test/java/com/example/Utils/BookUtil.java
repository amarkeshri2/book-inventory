package com.example.Utils;

import com.example.controller.book.Book;
import com.example.controller.request.BookUpdateRequest;
import com.example.controller.response.BookResponse;
import com.example.dto.BookDto;

import java.util.List;

public class BookUtil {
    public static List<BookResponse> getBookList(){
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

    public static BookDto getbookDto() {
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
}
