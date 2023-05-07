package eu.planlos.pretixtonextcloudintegrator.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditService implements IAuditService {

    private List<AuditEntry> webHookAuditRepository = new ArrayList<>();

    public AuditService() {
    }

    public void log(String message) {
        log.info(message);
        webHookAuditRepository.add(new AuditEntry(message));
    }

    public List<AuditEntry> getAfter(ZonedDateTime zdt) {
        return webHookAuditRepository.stream().filter(entry -> entry.getDate().isBefore(zdt)).collect(Collectors.toList());
    }
}