package com.example.common;

import com.example.common.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEventPayload implements Payload {
    private String bookId;
    private String title;
    private List<String> authors;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
    private String time;
    private String eventType;
}
