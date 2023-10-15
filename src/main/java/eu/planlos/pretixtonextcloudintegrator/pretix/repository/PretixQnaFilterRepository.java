package eu.planlos.pretixtonextcloudintegrator.pretix.repository;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PretixQnaFilterRepository extends JpaRepository<PretixQnaFilter, Long> {
}
