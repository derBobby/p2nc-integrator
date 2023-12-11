package eu.planlos.p2ncintegrator.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(AuditEntryController.URL_AUDIT_ENTRY)
@Slf4j
public class AuditEntryController {

    public static final String URL_AUDIT_ENTRY = "/api/v1/audit-entry";

    private final AuditService auditService;

    public AuditEntryController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/")
    public ResponseEntity<List<AuditEntry>> getAll() {
        log.info("Read all audit entries");
        return ResponseEntity.ok().body(auditService.getAll());
    }

    @GetMapping
    public ResponseEntity<List<AuditEntry>> getAll(@RequestParam ZonedDateTime zdt) {
        log.info("Read all audit entries after {}", zdt.toString());
        return ResponseEntity.ok().body(auditService.getAfter(zdt));
    }
}
