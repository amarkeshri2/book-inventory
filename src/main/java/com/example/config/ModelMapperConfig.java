package com.example.config;

import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;

public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
