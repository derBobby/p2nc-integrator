package eu.planlos.p2ncintegrator.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
    List<AuditEntry> findByDateAfter(ZonedDateTime zdt);
}
