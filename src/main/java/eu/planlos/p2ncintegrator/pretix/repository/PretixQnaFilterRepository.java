package eu.planlos.p2ncintegrator.pretix.repository;

import eu.planlos.p2ncintegrator.pretix.model.PretixQnaFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PretixQnaFilterRepository extends JpaRepository<PretixQnaFilter, Long> {
}
