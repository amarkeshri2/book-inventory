package com.example.googleBook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleBookItem {
    @JsonProperty("id")
    private String id;

    @JsonProperty("volumeInfo")
    private VolumeInfo volumeInfo;

    @JsonIgnore
    private Map<Object, Object> additionalProperties = new HashMap<Object, Object>();

}
