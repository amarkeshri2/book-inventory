package com.example.dao;

import com.example.common.ObjectTranslator;
import com.example.dao.entity.AuditEntity;
import com.example.dao.repository.AuditRepository;
import com.example.dto.AuditDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
public class AuditDao {
    private final ObjectTranslator translator;
    private final AuditRepository auditRepository;
    private static final Logger log = LoggerFactory.getLogger(AuditDao.class);
    public void save(AuditDto auditDto) {
        AuditEntity auditEntity = translator.translateToAuditEntity(auditDto);
        auditRepository.save(auditEntity).subscribe();
    }
    public Flux<AuditDto> getAudits(String id){
        return auditRepository.findByBookId(id)
                .map(audit -> translator.translate(audit, AuditDto.class));
    }
}
