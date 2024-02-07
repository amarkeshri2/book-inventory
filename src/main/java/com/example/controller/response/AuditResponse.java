package com.example.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditResponse {
    private String bookId;
    private String type;
    private String time;
}
