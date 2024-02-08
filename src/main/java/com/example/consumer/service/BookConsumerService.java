package com.example.consumer.service;

import com.example.common.BookEventPayload;
import com.example.common.ObjectTranslator;
import com.example.dao.AuditDao;
import com.example.dto.AuditDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookConsumerService {

    private final ObjectMapper objectMapper;
    private final ObjectTranslator translator;
    private final AuditDao auditDao;
    private static final Logger log = LoggerFactory.getLogger(BookConsumerService.class);
    public void process(String book) throws JsonProcessingException {
        BookEventPayload eventPayload = objectMapper.readValue(book, BookEventPayload.class);
        log.info("processing book payload : {} ", eventPayload);
        AuditDto auditDto = translator.translate(eventPayload, AuditDto.class);
        auditDao.save(auditDto);
    }
}
