package eu.planlos.p2ncintegrator.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class AuditService implements IAuditService {

    private final AuditRepository webHookAuditRepository;

    public AuditService(AuditRepository webHookAuditRepository) {
        this.webHookAuditRepository = webHookAuditRepository;
    }

    public void log(String message) {
        log.info(message);
        webHookAuditRepository.save(new AuditEntry(message));
    }

    @Override
    public List<AuditEntry> getAll() {
        return webHookAuditRepository.findAll();
    }

    @Override
    public List<AuditEntry> getAfter(ZonedDateTime zdt) {
        return webHookAuditRepository.findByDateAfter(zdt);
    }
}