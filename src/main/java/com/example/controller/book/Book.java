package com.example.controller.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @NotNull
    private String bookId;
    @NotNull
    private String title;
    @NotNull
    private List<String> authors;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
}
