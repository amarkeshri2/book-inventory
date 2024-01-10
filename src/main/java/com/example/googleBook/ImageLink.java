package com.example.googleBook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageLink {
    @JsonProperty("thumbnail")
    private String image;
    @JsonIgnore
    private Map<Object, Object> additionalProperties = new HashMap<Object, Object>();

}
