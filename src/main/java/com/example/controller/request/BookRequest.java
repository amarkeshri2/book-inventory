package com.example.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotNull
    private String bookId;

    @NotBlank
    @Size(min= 1 , max = 128, message = "Book title maximum length exceeded")
    private String title;

    @NotBlank
    private List<String> authors;

    private String image;

    private String description;

    @NotNull
    @Positive(message = "Book price cannot be negative or zero")
    private Double price;

    @NotNull
    @Positive(message = "Book quantity cannot be negative or zero")
    private Integer quantity;
}
