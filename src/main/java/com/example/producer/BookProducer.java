package com.example.producer;

import com.example.common.Payload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookProducer {
    private static final Logger log = LoggerFactory.getLogger(BookProducer.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${spring.kafka.topic}")
    private String topic;
    public void sendEvent(Payload eventPayload) throws JsonProcessingException {
        log.info("Start of kafka");
        String payload = objectMapper.writeValueAsString(eventPayload);
        this.kafkaTemplate.send(topic, payload);
        log.info("event sent on kafka : {}", payload);
        log.info("End of kafka");
    }
}
