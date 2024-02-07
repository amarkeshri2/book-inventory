package com.example.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Audit")
public class AuditEntity {
    @Id
    private String id;
    private String bookId;
    private String type;
    private String time;
}
