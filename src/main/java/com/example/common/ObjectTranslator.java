package com.example.common;


import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
public class ObjectTranslator {
    @Autowired
    @Qualifier("modelMapper")
    private ModelMapper modelMapper;

    public <T, R> R translate(T source, Class<R> targetType) {
        return modelMapper.map(source, targetType);
    }

    public <T, R> void translate(T source, R target) {
        modelMapper.map(source, target);
    }

    public <T, R> List<R> translateToList(List<T> entities, Class<R> targetType) {
        return entities.stream()
                .map(source -> modelMapper.map(source, targetType))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
