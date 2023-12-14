package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private String title;
    private List<String> authors;
    private String image;
    private String description;
    private double price;
    private int quantity;
}
