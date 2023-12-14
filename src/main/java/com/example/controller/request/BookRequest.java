package com.example.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @Valid
    @NotBlank
    @Size(min= 1 , max = 128, message = "Book title maximum length exceeded")
    private String title;
    @Valid
    @NotBlank
    private List<String> authors;
    @Valid
    private String image;
    @Valid
    private String description;
    @Valid
    @Positive(message = "Book price cannot be negative or zero")
    private Double price;
    @Valid
    @Positive(message = "Book quantity cannot be negative or zero")
    private Integer quantity;
}
