package com.example.consumer;

import com.example.controller.book.Book;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class BookConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BookConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "ContainerFactory"
    )
    public void consumeEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            String book = record.value();
            logger.info("Received book event: {}", book);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing book event", e);
        }
    }
}
