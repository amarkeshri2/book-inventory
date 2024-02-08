package com.example.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateRequest {
    @Positive(message = "Book price cannot be negative or zero")
    private Double price;
    @Positive(message = "Book quantity cannot be negative or zero")
    private Integer quantity;
}
