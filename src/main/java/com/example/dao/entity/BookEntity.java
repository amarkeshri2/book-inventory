package com.example.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Book")
public class BookEntity {
    @Id
    private String id;
    private String bookId;
    private String title;
    private List<String> authors;
    private String image;
    private String description;
    private double price;
    private int quantity;
}
