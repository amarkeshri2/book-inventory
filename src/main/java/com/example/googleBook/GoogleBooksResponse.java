package com.example.googleBook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleBooksResponse {
    @JsonProperty("kind")
    private String kind;

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("items")
    private List<GoogleBookItem> items;

    @JsonIgnore
    private Map<Object, Object> additionalProperties = new HashMap<Object, Object>();

}
