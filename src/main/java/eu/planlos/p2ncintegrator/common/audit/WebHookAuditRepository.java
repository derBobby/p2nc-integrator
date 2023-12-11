package eu.planlos.p2ncintegrator.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface WebHookAuditRepository extends JpaRepository<AuditEntry, Long> {
    List<AuditEntry> findByDateGreaterThan(ZonedDateTime zdt);
}