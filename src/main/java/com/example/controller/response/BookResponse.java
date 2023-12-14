package com.example.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private String title;
    private List<String> authors;
    private String description;
    private String image;
    private Integer Quantity;
    private Double price;
}
