package com.example.dao;

import com.example.Utils.BookUtil;
import com.example.common.ObjectTranslator;
import com.example.dao.entity.AuditEntity;
import com.example.dao.repository.AuditRepository;
import com.example.dto.AuditDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.Mockito.*;

public class AuditDaoTest {

    @InjectMocks
    private AuditDao auditDao;

    @Mock
    private AuditRepository auditRepository;
    @Mock
    private ObjectTranslator translator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAudit() {
        AuditEntity entity = new AuditEntity("65c3254ff331c26240d88378", "1L", "CREATE", "2024-02-07T12:12:47.354055300");
        AuditDto dto = new AuditDto("1L", "CREATE", "2024-02-07T12:12:47.354055300");
        when(translator.translateToAuditEntity(dto)).thenReturn(entity);
        when(auditRepository.save(entity)).thenReturn(Mono.just(entity));

        auditDao.save(dto);
        verify(translator).translateToAuditEntity(dto);
        verify(auditRepository).save(entity);
    }

    @Test
    void testGetAuditsByBookId() {
        AuditEntity entity = new AuditEntity("65c3254ff331c26240d88378", "1L", "CREATE", "2024-02-07T12:12:47.354055300");
        AuditDto dto = new AuditDto("1L", "CREATE", "2024-02-07T12:12:47.354055300");
        String bookId = "1L";
        when(auditRepository.findByBookId(bookId)).thenReturn(Flux.just(entity));
        when(translator.translate(any(AuditEntity.class), Mockito.eq(AuditDto.class))).thenReturn(dto);


        Flux<AuditDto> result = auditDao.getAudits(bookId);

        StepVerifier.create(result)
                .expectNext(dto)
                .verifyComplete();

    }



}
