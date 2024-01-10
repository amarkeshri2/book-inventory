//package com.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfiguration {
//    private static final String GOOGLE_BOOK_API_URI = "https://www.googleapis.com/books/v1/volumes";
//    @Bean
//    public WebClient webClient() {
//        final int size = 16 * 1024 * 1024;
//        final ExchangeStrategies strategies = ExchangeStrategies.builder()
//                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
//                .build();
//
//        return WebClient
//                .builder()
//                .baseUrl(GOOGLE_BOOK_API_URI)
//                .defaultCookie("cookieKey", "cookieValue")
//                .exchangeStrategies(strategies)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }
//
//
//}
